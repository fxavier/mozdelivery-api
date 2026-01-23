package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

/**
 * Enumeration representing the status of a delivery person.
 */
public enum DeliveryPersonStatus {
    
    /**
     * Available for new deliveries.
     */
    AVAILABLE,
    
    /**
     * Currently busy with deliveries (at capacity).
     */
    BUSY,
    
    /**
     * On break or temporarily unavailable.
     */
    ON_BREAK,
    
    /**
     * Off duty for the day.
     */
    OFF_DUTY,
    
    /**
     * Inactive (suspended, terminated, etc.).
     */
    INACTIVE;
    
    /**
     * Check if this status allows accepting new deliveries.
     */
    public boolean isAvailable() {
        return this == AVAILABLE;
    }
    
    /**
     * Check if this status represents an active delivery person.
     */
    public boolean isActive() {
        return this == AVAILABLE || this == BUSY || this == ON_BREAK;
    }
    
    /**
     * Check if status can transition to the given new status.
     */
    public boolean canTransitionTo(DeliveryPersonStatus newStatus) {
        return switch (this) {
            case AVAILABLE -> newStatus == BUSY || newStatus == ON_BREAK || 
                             newStatus == OFF_DUTY || newStatus == INACTIVE;
            case BUSY -> newStatus == AVAILABLE || newStatus == ON_BREAK || 
                        newStatus == OFF_DUTY || newStatus == INACTIVE;
            case ON_BREAK -> newStatus == AVAILABLE || newStatus == OFF_DUTY || 
                            newStatus == INACTIVE;
            case OFF_DUTY -> newStatus == AVAILABLE || newStatus == INACTIVE;
            case INACTIVE -> newStatus == AVAILABLE; // Can be reactivated
        };
    }
}