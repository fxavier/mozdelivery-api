package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Route value object representing an ordered sequence of locations with associated metadata.
 * Used for delivery route planning and optimization.
 */
public final class Route implements ValueObject {
    
    @NotNull
    @Size(min = 2, message = "Route must have at least 2 waypoints (start and end)")
    private final List<Waypoint> waypoints;
    
    @NotNull
    private final Distance totalDistance;
    
    @NotNull
    private final Duration estimatedDuration;
    
    private Route(List<Waypoint> waypoints, Distance totalDistance, Duration estimatedDuration) {
        this.waypoints = Collections.unmodifiableList(new ArrayList<>(waypoints));
        this.totalDistance = totalDistance;
        this.estimatedDuration = estimatedDuration;
    }
    
    public static Route of(List<Waypoint> waypoints, Distance totalDistance, Duration estimatedDuration) {
        Objects.requireNonNull(waypoints, "Waypoints cannot be null");
        Objects.requireNonNull(totalDistance, "Total distance cannot be null");
        Objects.requireNonNull(estimatedDuration, "Estimated duration cannot be null");
        
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("Route must have at least 2 waypoints (start and end)");
        }
        
        // Validate that all waypoints are not null
        for (Waypoint waypoint : waypoints) {
            Objects.requireNonNull(waypoint, "Waypoint cannot be null");
        }
        
        return new Route(waypoints, totalDistance, estimatedDuration);
    }
    
    /**
     * Create a simple route from a list of locations with calculated distance.
     */
    public static Route fromLocations(List<Location> locations, DistanceCalculationService distanceService) {
        Objects.requireNonNull(locations, "Locations cannot be null");
        Objects.requireNonNull(distanceService, "Distance service cannot be null");
        
        if (locations.size() < 2) {
            throw new IllegalArgumentException("Route must have at least 2 locations");
        }
        
        List<Waypoint> waypoints = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            WaypointType type = i == 0 ? WaypointType.START : 
                               i == locations.size() - 1 ? WaypointType.END : 
                               WaypointType.INTERMEDIATE;
            waypoints.add(Waypoint.of(locations.get(i), type));
        }
        
        Distance totalDistance = distanceService.calculateRouteDistance(locations);
        
        // Estimate duration based on average speed (30 km/h for city driving)
        double averageSpeedKmh = 30.0;
        double durationHours = totalDistance.getKilometers().doubleValue() / averageSpeedKmh;
        Duration estimatedDuration = Duration.ofMinutes((long) (durationHours * 60));
        
        return new Route(waypoints, totalDistance, estimatedDuration);
    }
    
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }
    
    public Distance getTotalDistance() {
        return totalDistance;
    }
    
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }
    
    /**
     * Get the starting location of the route.
     */
    public Location getStartLocation() {
        return waypoints.get(0).getLocation();
    }
    
    /**
     * Get the ending location of the route.
     */
    public Location getEndLocation() {
        return waypoints.get(waypoints.size() - 1).getLocation();
    }
    
    /**
     * Get all locations in the route order.
     */
    public List<Location> getLocations() {
        return waypoints.stream()
                .map(Waypoint::getLocation)
                .toList();
    }
    
    /**
     * Get the number of waypoints in the route.
     */
    public int getWaypointCount() {
        return waypoints.size();
    }
    
    /**
     * Check if this route contains a specific location.
     */
    public boolean containsLocation(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        return waypoints.stream()
                .anyMatch(waypoint -> waypoint.getLocation().equals(location));
    }
    
    /**
     * Get the estimated speed for this route in km/h.
     */
    public double getAverageSpeedKmh() {
        if (estimatedDuration.isZero()) {
            return 0.0;
        }
        
        double durationHours = estimatedDuration.toMinutes() / 60.0;
        return totalDistance.getKilometers().doubleValue() / durationHours;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(waypoints, route.waypoints) &&
               Objects.equals(totalDistance, route.totalDistance) &&
               Objects.equals(estimatedDuration, route.estimatedDuration);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(waypoints, totalDistance, estimatedDuration);
    }
    
    @Override
    public String toString() {
        return String.format("Route{waypoints=%d, distance=%s, duration=%s}", 
                           waypoints.size(), totalDistance, estimatedDuration);
    }
}