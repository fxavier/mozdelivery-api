package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing delivery capacity constraints.
 */
public record DeliveryCapacity(
    int maxOrders,
    int maxWeight, // in grams
    int maxVolume  // in cubic centimeters
) implements ValueObject {
    
    public DeliveryCapacity {
        if (maxOrders <= 0) {
            throw new IllegalArgumentException("Max orders must be positive");
        }
        if (maxWeight <= 0) {
            throw new IllegalArgumentException("Max weight must be positive");
        }
        if (maxVolume <= 0) {
            throw new IllegalArgumentException("Max volume must be positive");
        }
    }
    
    public static DeliveryCapacity of(int maxOrders, int maxWeight, int maxVolume) {
        return new DeliveryCapacity(maxOrders, maxWeight, maxVolume);
    }
    
    /**
     * Create default capacity for a delivery person.
     */
    public static DeliveryCapacity defaultCapacity() {
        return new DeliveryCapacity(
            5,          // 5 orders max
            10000,      // 10kg max
            50000       // 50L max
        );
    }
    
    /**
     * Check if this capacity can accommodate the given load.
     */
    public boolean canAccommodate(int orders, int weight, int volume) {
        return orders <= maxOrders && weight <= maxWeight && volume <= maxVolume;
    }
    
    /**
     * Calculate remaining capacity after subtracting the given load.
     */
    public DeliveryCapacity subtract(int orders, int weight, int volume) {
        int remainingOrders = Math.max(0, maxOrders - orders);
        int remainingWeight = Math.max(0, maxWeight - weight);
        int remainingVolume = Math.max(0, maxVolume - volume);
        
        return new DeliveryCapacity(remainingOrders, remainingWeight, remainingVolume);
    }
    
    /**
     * Check if this capacity is fully utilized.
     */
    public boolean isFullyUtilized() {
        return maxOrders == 0 || maxWeight == 0 || maxVolume == 0;
    }
    
    /**
     * Get utilization percentage (0.0 to 1.0) based on the most constraining factor.
     */
    public double getUtilizationPercentage(int currentOrders, int currentWeight, int currentVolume) {
        double orderUtilization = (double) currentOrders / maxOrders;
        double weightUtilization = (double) currentWeight / maxWeight;
        double volumeUtilization = (double) currentVolume / maxVolume;
        
        return Math.max(Math.max(orderUtilization, weightUtilization), volumeUtilization);
    }
}