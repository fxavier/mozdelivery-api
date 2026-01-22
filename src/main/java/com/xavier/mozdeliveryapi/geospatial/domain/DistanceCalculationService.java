package com.xavier.mozdeliveryapi.geospatial.domain;

import java.util.List;

/**
 * Domain service for advanced distance calculations and routing operations.
 * Provides methods for calculating distances, routes, and travel times.
 */
public interface DistanceCalculationService {
    
    /**
     * Calculate the straight-line (Haversine) distance between two locations.
     * @param from the starting location
     * @param to the destination location
     * @return the straight-line distance
     */
    Distance calculateStraightLineDistance(Location from, Location to);
    
    /**
     * Calculate the road distance between two locations.
     * This may involve calling external routing services.
     * @param from the starting location
     * @param to the destination location
     * @return the road distance, or straight-line distance if routing fails
     */
    Distance calculateRoadDistance(Location from, Location to);
    
    /**
     * Calculate distances from one location to multiple destinations.
     * @param from the starting location
     * @param destinations the list of destination locations
     * @return list of distances in the same order as destinations
     */
    List<Distance> calculateDistancesToMultipleDestinations(Location from, List<Location> destinations);
    
    /**
     * Find the closest location from a list of candidates.
     * @param from the reference location
     * @param candidates the list of candidate locations
     * @return the closest location from the candidates
     * @throws IllegalArgumentException if candidates list is empty
     */
    Location findClosestLocation(Location from, List<Location> candidates);
    
    /**
     * Calculate the total distance for a route through multiple locations.
     * @param locations the ordered list of locations forming the route
     * @return the total distance of the route
     * @throws IllegalArgumentException if locations list has fewer than 2 elements
     */
    Distance calculateRouteDistance(List<Location> locations);
    
    /**
     * Check if a location is within a certain distance of another location.
     * @param location1 the first location
     * @param location2 the second location
     * @param maxDistance the maximum allowed distance
     * @return true if the locations are within the specified distance
     */
    boolean isWithinDistance(Location location1, Location location2, Distance maxDistance);
    
    /**
     * Get all locations within a certain radius of a center location.
     * @param center the center location
     * @param candidates the list of candidate locations to check
     * @param radius the search radius
     * @return list of locations within the radius
     */
    List<Location> getLocationsWithinRadius(Location center, List<Location> candidates, Distance radius);
}