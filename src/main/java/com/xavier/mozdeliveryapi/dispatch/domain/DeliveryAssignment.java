package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.Distance;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a delivery assignment result.
 */
public record DeliveryAssignment(
    DeliveryPersonId deliveryPersonId,
    Distance distanceToPickup,
    double capacityUtilization,
    int priority,
    Instant assignedAt
) implements ValueObject {
    
    public DeliveryAssignment {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(distanceToPickup, "Distance to pickup cannot be null");
        Objects.requireNonNull(assignedAt, "Assigned at cannot be null");
        
        if (capacityUtilization < 0.0 || capacityUtilization > 1.0) {
            throw new IllegalArgumentException("Capacity utilization must be between 0.0 and 1.0");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Priority cannot be negative");
        }
    }
    
    public static DeliveryAssignment of(DeliveryPersonId deliveryPersonId, 
                                       Distance distanceToPickup,
                                       double capacityUtilization, 
                                       int priority) {
        return new DeliveryAssignment(deliveryPersonId, distanceToPickup, 
                                     capacityUtilization, priority, Instant.now());
    }
    
    /**
     * Calculate assignment score for optimization (higher is better).
     * Considers distance (closer is better) and capacity utilization (balanced is better).
     */
    public double getAssignmentScore() {
        // Distance score: closer is better (inverse relationship)
        double maxDistanceKm = 50.0; // Maximum reasonable distance
        double distanceScore = Math.max(0, 1.0 - (distanceToPickup.getKilometers().doubleValue() / maxDistanceKm));
        
        // Capacity score: prefer balanced utilization (not too empty, not too full)
        double optimalUtilization = 0.7;
        double capacityScore = 1.0 - Math.abs(capacityUtilization - optimalUtilization);
        
        // Priority score: higher priority gets higher score
        double priorityScore = priority / 10.0; // Normalize assuming max priority of 10
        
        // Weighted combination
        return (distanceScore * 0.5) + (capacityScore * 0.3) + (priorityScore * 0.2);
    }
}