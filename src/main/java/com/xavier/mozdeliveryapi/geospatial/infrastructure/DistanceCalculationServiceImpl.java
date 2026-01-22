package com.xavier.mozdeliveryapi.geospatial.infrastructure;

import com.xavier.mozdeliveryapi.geospatial.domain.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.DistanceCalculationService;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of DistanceCalculationService using Haversine formula for distance calculations.
 * In a production environment, this could be enhanced to use external routing services
 * for more accurate road distance calculations.
 */
@Service
public class DistanceCalculationServiceImpl implements DistanceCalculationService {
    
    @Override
    public Distance calculateStraightLineDistance(Location from, Location to) {
        Objects.requireNonNull(from, "From location cannot be null");
        Objects.requireNonNull(to, "To location cannot be null");
        
        return from.distanceTo(to);
    }
    
    @Override
    public Distance calculateRoadDistance(Location from, Location to) {
        Objects.requireNonNull(from, "From location cannot be null");
        Objects.requireNonNull(to, "To location cannot be null");
        
        // For now, return straight-line distance
        // In production, this would call external routing services like Google Maps, Mapbox, etc.
        Distance straightLineDistance = calculateStraightLineDistance(from, to);
        
        // Apply a rough factor to estimate road distance (typically 1.2-1.5x straight line)
        return straightLineDistance.multiply(java.math.BigDecimal.valueOf(1.3));
    }
    
    @Override
    public List<Distance> calculateDistancesToMultipleDestinations(Location from, List<Location> destinations) {
        Objects.requireNonNull(from, "From location cannot be null");
        Objects.requireNonNull(destinations, "Destinations cannot be null");
        
        return destinations.stream()
                .map(destination -> calculateStraightLineDistance(from, destination))
                .collect(Collectors.toList());
    }
    
    @Override
    public Location findClosestLocation(Location from, List<Location> candidates) {
        Objects.requireNonNull(from, "From location cannot be null");
        Objects.requireNonNull(candidates, "Candidates cannot be null");
        
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("Candidates list cannot be empty");
        }
        
        Location closest = candidates.get(0);
        Distance minDistance = calculateStraightLineDistance(from, closest);
        
        for (int i = 1; i < candidates.size(); i++) {
            Location candidate = candidates.get(i);
            Distance distance = calculateStraightLineDistance(from, candidate);
            
            if (distance.isLessThan(minDistance)) {
                closest = candidate;
                minDistance = distance;
            }
        }
        
        return closest;
    }
    
    @Override
    public Distance calculateRouteDistance(List<Location> locations) {
        Objects.requireNonNull(locations, "Locations cannot be null");
        
        if (locations.size() < 2) {
            throw new IllegalArgumentException("Route must have at least 2 locations");
        }
        
        Distance totalDistance = Distance.zero();
        
        for (int i = 0; i < locations.size() - 1; i++) {
            Distance segmentDistance = calculateStraightLineDistance(locations.get(i), locations.get(i + 1));
            totalDistance = totalDistance.add(segmentDistance);
        }
        
        return totalDistance;
    }
    
    @Override
    public boolean isWithinDistance(Location location1, Location location2, Distance maxDistance) {
        Objects.requireNonNull(location1, "Location1 cannot be null");
        Objects.requireNonNull(location2, "Location2 cannot be null");
        Objects.requireNonNull(maxDistance, "Max distance cannot be null");
        
        Distance actualDistance = calculateStraightLineDistance(location1, location2);
        return actualDistance.isLessThan(maxDistance) || actualDistance.equals(maxDistance);
    }
    
    @Override
    public List<Location> getLocationsWithinRadius(Location center, List<Location> candidates, Distance radius) {
        Objects.requireNonNull(center, "Center location cannot be null");
        Objects.requireNonNull(candidates, "Candidates cannot be null");
        Objects.requireNonNull(radius, "Radius cannot be null");
        
        return candidates.stream()
                .filter(candidate -> isWithinDistance(center, candidate, radius))
                .collect(Collectors.toList());
    }
}