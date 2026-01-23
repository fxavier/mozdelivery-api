package com.xavier.mozdeliveryapi.geospatial.infra.persistence;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.geospatial.domain.entity.DistanceCalculationService;
import com.xavier.mozdeliveryapi.geospatial.application.usecase.port.RouteOptimizer;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.TrafficConditions;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.WaypointType;

/**
 * Implementation of RouteOptimizer using heuristic algorithms.
 * Uses nearest neighbor algorithm for route optimization and various
 * heuristics for delivery time estimation.
 */
@Service
public class RouteOptimizerImpl implements RouteOptimizer {
    
    private final DistanceCalculationService distanceCalculationService;
    
    public RouteOptimizerImpl(DistanceCalculationService distanceCalculationService) {
        this.distanceCalculationService = Objects.requireNonNull(distanceCalculationService, 
            "Distance calculation service cannot be null");
    }
    
    @Override
    public Route optimizeRoute(Location startLocation, List<Location> deliveryLocations, Location endLocation) {
        Objects.requireNonNull(startLocation, "Start location cannot be null");
        Objects.requireNonNull(deliveryLocations, "Delivery locations cannot be null");
        Objects.requireNonNull(endLocation, "End location cannot be null");
        
        if (deliveryLocations.isEmpty()) {
            // Simple route from start to end
            return Route.fromLocations(List.of(startLocation, endLocation), distanceCalculationService);
        }
        
        // Use nearest neighbor algorithm to find optimal order
        List<Location> optimizedOrder = findOptimalOrder(startLocation, deliveryLocations);
        
        // Build complete route: start -> optimized deliveries -> end
        List<Location> completeRoute = new ArrayList<>();
        completeRoute.add(startLocation);
        completeRoute.addAll(optimizedOrder);
        if (!endLocation.equals(startLocation)) {
            completeRoute.add(endLocation);
        }
        
        return Route.fromLocations(completeRoute, distanceCalculationService);
    }
    
    @Override
    public Route optimizeRouteWithTimeConstraint(Location startLocation, List<Location> deliveryLocations, 
                                               Location endLocation, Duration maxDuration) {
        Objects.requireNonNull(maxDuration, "Max duration cannot be null");
        
        Route optimizedRoute = optimizeRoute(startLocation, deliveryLocations, endLocation);
        
        // Check if route meets time constraint
        Duration estimatedDuration = estimateDeliveryTime(optimizedRoute, Duration.ofMinutes(5)); // 5 min per stop
        
        if (estimatedDuration.compareTo(maxDuration) <= 0) {
            return optimizedRoute;
        }
        
        // If route is too long, try to reduce delivery locations
        // This is a simplified approach - in production, more sophisticated algorithms would be used
        List<Location> reducedDeliveries = new ArrayList<>(deliveryLocations);
        
        while (!reducedDeliveries.isEmpty() && estimatedDuration.compareTo(maxDuration) > 0) {
            // Remove the delivery location that's furthest from the route
            Location furthest = findFurthestLocation(startLocation, reducedDeliveries);
            reducedDeliveries.remove(furthest);
            
            optimizedRoute = optimizeRoute(startLocation, reducedDeliveries, endLocation);
            estimatedDuration = estimateDeliveryTime(optimizedRoute, Duration.ofMinutes(5));
        }
        
        return optimizedRoute;
    }
    
    @Override
    public Duration estimateDeliveryTime(Route route, Duration averageStopDuration) {
        Objects.requireNonNull(route, "Route cannot be null");
        Objects.requireNonNull(averageStopDuration, "Average stop duration cannot be null");
        
        Duration travelTime = route.getEstimatedDuration();
        
        // Count delivery stops (exclude start and end)
        long deliveryStops = route.getWaypoints().stream()
                .filter(waypoint -> waypoint.getType() == WaypointType.DELIVERY || 
                                  waypoint.getType() == WaypointType.INTERMEDIATE)
                .count();
        
        Duration totalStopTime = averageStopDuration.multipliedBy(deliveryStops);
        
        return travelTime.plus(totalStopTime);
    }
    
    @Override
    public List<Location> findOptimalOrder(Location startLocation, List<Location> locations) {
        Objects.requireNonNull(startLocation, "Start location cannot be null");
        Objects.requireNonNull(locations, "Locations cannot be null");
        
        if (locations.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (locations.size() == 1) {
            return new ArrayList<>(locations);
        }
        
        // Nearest neighbor algorithm
        List<Location> unvisited = new ArrayList<>(locations);
        List<Location> optimizedOrder = new ArrayList<>();
        Location currentLocation = startLocation;
        
        while (!unvisited.isEmpty()) {
            Location nearest = findNearestLocation(currentLocation, unvisited);
            optimizedOrder.add(nearest);
            unvisited.remove(nearest);
            currentLocation = nearest;
        }
        
        return optimizedOrder;
    }
    
    @Override
    public List<Route> calculateRouteOptions(Location startLocation, List<Location> deliveryLocations, 
                                           Location endLocation, int maxRoutes) {
        Objects.requireNonNull(startLocation, "Start location cannot be null");
        Objects.requireNonNull(deliveryLocations, "Delivery locations cannot be null");
        Objects.requireNonNull(endLocation, "End location cannot be null");
        
        if (maxRoutes <= 0) {
            throw new IllegalArgumentException("Max routes must be positive");
        }
        
        if (deliveryLocations.size() <= 1) {
            // Only one possible route
            Route route = optimizeRoute(startLocation, deliveryLocations, endLocation);
            return List.of(route);
        }
        
        List<Route> routeOptions = new ArrayList<>();
        
        // Generate different route permutations (limited to avoid exponential explosion)
        int permutationsToTry = Math.min(maxRoutes * 2, factorial(Math.min(deliveryLocations.size(), 8)));
        
        Set<List<Location>> triedPermutations = new HashSet<>();
        Random random = new Random();
        
        for (int i = 0; i < permutationsToTry && routeOptions.size() < maxRoutes; i++) {
            List<Location> shuffledDeliveries = new ArrayList<>(deliveryLocations);
            Collections.shuffle(shuffledDeliveries, random);
            
            if (!triedPermutations.contains(shuffledDeliveries)) {
                triedPermutations.add(new ArrayList<>(shuffledDeliveries));
                
                List<Location> completeRoute = new ArrayList<>();
                completeRoute.add(startLocation);
                completeRoute.addAll(shuffledDeliveries);
                if (!endLocation.equals(startLocation)) {
                    completeRoute.add(endLocation);
                }
                
                Route route = Route.fromLocations(completeRoute, distanceCalculationService);
                routeOptions.add(route);
            }
        }
        
        // Always include the optimized route
        Route optimizedRoute = optimizeRoute(startLocation, deliveryLocations, endLocation);
        if (!routeOptions.contains(optimizedRoute)) {
            routeOptions.add(optimizedRoute);
        }
        
        // Sort by total distance (best first)
        return routeOptions.stream()
                .sorted(Comparator.comparing(Route::getTotalDistance))
                .limit(maxRoutes)
                .collect(Collectors.toList());
    }
    
    @Override
    public Route optimizeForTraffic(Route route, TrafficConditions trafficConditions) {
        Objects.requireNonNull(route, "Route cannot be null");
        Objects.requireNonNull(trafficConditions, "Traffic conditions cannot be null");
        
        // Adjust the estimated duration based on traffic conditions
        Duration adjustedDuration = trafficConditions.adjustDuration(route.getEstimatedDuration());
        
        // Create new route with adjusted duration
        return Route.of(route.getWaypoints(), route.getTotalDistance(), adjustedDuration);
    }
    
    @Override
    public boolean isRouteFeasible(Route route, Distance maxDistance, Duration maxDuration) {
        Objects.requireNonNull(route, "Route cannot be null");
        Objects.requireNonNull(maxDistance, "Max distance cannot be null");
        Objects.requireNonNull(maxDuration, "Max duration cannot be null");
        
        return route.getTotalDistance().isLessThan(maxDistance) || route.getTotalDistance().equals(maxDistance) &&
               route.getEstimatedDuration().compareTo(maxDuration) <= 0;
    }
    
    @Override
    public List<Route> splitRouteIfNeeded(Route route, int maxWaypoints, Distance maxDistance) {
        Objects.requireNonNull(route, "Route cannot be null");
        Objects.requireNonNull(maxDistance, "Max distance cannot be null");
        
        if (maxWaypoints <= 0) {
            throw new IllegalArgumentException("Max waypoints must be positive");
        }
        
        // Check if route needs splitting
        if (route.getWaypointCount() <= maxWaypoints && 
            (route.getTotalDistance().isLessThan(maxDistance) || route.getTotalDistance().equals(maxDistance))) {
            return List.of(route);
        }
        
        // Split route into smaller segments
        List<Route> splitRoutes = new ArrayList<>();
        List<Location> locations = route.getLocations();
        Location startLocation = locations.get(0);
        Location endLocation = locations.get(locations.size() - 1);
        
        // Split delivery locations into chunks
        List<Location> deliveryLocations = locations.subList(1, locations.size() - 1);
        
        int chunkSize = maxWaypoints - 2; // Reserve space for start and end
        for (int i = 0; i < deliveryLocations.size(); i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, deliveryLocations.size());
            List<Location> chunk = deliveryLocations.subList(i, endIndex);
            
            Route subRoute = optimizeRoute(startLocation, chunk, endLocation);
            splitRoutes.add(subRoute);
        }
        
        return splitRoutes;
    }
    
    // Helper methods
    
    private Location findNearestLocation(Location from, List<Location> candidates) {
        return distanceCalculationService.findClosestLocation(from, candidates);
    }
    
    private Location findFurthestLocation(Location from, List<Location> candidates) {
        Location furthest = candidates.get(0);
        Distance maxDistance = distanceCalculationService.calculateStraightLineDistance(from, furthest);
        
        for (int i = 1; i < candidates.size(); i++) {
            Location candidate = candidates.get(i);
            Distance distance = distanceCalculationService.calculateStraightLineDistance(from, candidate);
            
            if (distance.isGreaterThan(maxDistance)) {
                furthest = candidate;
                maxDistance = distance;
            }
        }
        
        return furthest;
    }
    
    private int factorial(int n) {
        if (n <= 1) return 1;
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}