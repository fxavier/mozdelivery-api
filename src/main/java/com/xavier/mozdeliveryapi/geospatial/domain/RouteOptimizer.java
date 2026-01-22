package com.xavier.mozdeliveryapi.geospatial.domain;

import java.time.Duration;
import java.util.List;

/**
 * Domain service interface for route optimization operations.
 * Provides methods for calculating optimal routes, estimating delivery times,
 * and optimizing multi-stop delivery routes.
 */
public interface RouteOptimizer {
    
    /**
     * Calculate the optimal route through multiple delivery locations.
     * Uses algorithms like nearest neighbor or more sophisticated optimization.
     * 
     * @param startLocation the starting location (e.g., warehouse)
     * @param deliveryLocations the list of delivery destinations
     * @param endLocation the ending location (can be same as start for round trips)
     * @return optimized route through all locations
     */
    Route optimizeRoute(Location startLocation, List<Location> deliveryLocations, Location endLocation);
    
    /**
     * Calculate the optimal route for multiple deliveries with time constraints.
     * 
     * @param startLocation the starting location
     * @param deliveryLocations the list of delivery destinations
     * @param endLocation the ending location
     * @param maxDuration maximum allowed route duration
     * @return optimized route within time constraints, or empty if impossible
     */
    Route optimizeRouteWithTimeConstraint(Location startLocation, List<Location> deliveryLocations, 
                                        Location endLocation, Duration maxDuration);
    
    /**
     * Estimate the total delivery time for a route including stop times.
     * 
     * @param route the route to estimate
     * @param averageStopDuration average time spent at each delivery location
     * @return estimated total time including travel and stops
     */
    Duration estimateDeliveryTime(Route route, Duration averageStopDuration);
    
    /**
     * Find the most efficient order to visit a list of locations.
     * This is a simplified version of the Traveling Salesman Problem.
     * 
     * @param startLocation the starting location
     * @param locations the locations to visit
     * @return ordered list of locations for optimal route
     */
    List<Location> findOptimalOrder(Location startLocation, List<Location> locations);
    
    /**
     * Calculate multiple route options and return them sorted by efficiency.
     * 
     * @param startLocation the starting location
     * @param deliveryLocations the delivery destinations
     * @param endLocation the ending location
     * @param maxRoutes maximum number of route options to return
     * @return list of route options sorted by total distance (best first)
     */
    List<Route> calculateRouteOptions(Location startLocation, List<Location> deliveryLocations, 
                                    Location endLocation, int maxRoutes);
    
    /**
     * Optimize a route considering traffic conditions and time of day.
     * 
     * @param route the base route to optimize
     * @param trafficConditions current traffic conditions
     * @return optimized route considering traffic
     */
    Route optimizeForTraffic(Route route, TrafficConditions trafficConditions);
    
    /**
     * Check if a route is feasible within given constraints.
     * 
     * @param route the route to validate
     * @param maxDistance maximum allowed total distance
     * @param maxDuration maximum allowed total duration
     * @return true if the route meets all constraints
     */
    boolean isRouteFeasible(Route route, Distance maxDistance, Duration maxDuration);
    
    /**
     * Split a large route into multiple smaller routes if it exceeds capacity.
     * 
     * @param route the route to potentially split
     * @param maxWaypoints maximum waypoints per route
     * @param maxDistance maximum distance per route
     * @return list of routes (single route if no split needed)
     */
    List<Route> splitRouteIfNeeded(Route route, int maxWaypoints, Distance maxDistance);
}