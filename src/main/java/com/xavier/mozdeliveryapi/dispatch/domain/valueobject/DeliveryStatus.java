package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

/**
 * Enumeration representing the status of a delivery.
 */
public enum DeliveryStatus {
    
    /**
     * Delivery has been assigned but not yet started.
     */
    ASSIGNED,
    
    /**
     * Delivery person is en route to pickup location.
     */
    EN_ROUTE_TO_PICKUP,
    
    /**
     * Delivery person has arrived at pickup location.
     */
    ARRIVED_AT_PICKUP,
    
    /**
     * Order has been picked up and delivery is in progress.
     */
    IN_TRANSIT,
    
    /**
     * Delivery person has arrived at delivery location.
     */
    ARRIVED_AT_DELIVERY,
    
    /**
     * Delivery has been completed successfully.
     */
    DELIVERED,
    
    /**
     * Delivery was cancelled.
     */
    CANCELLED,
    
    /**
     * Delivery failed (customer not available, address issues, etc.).
     */
    FAILED;
    
    /**
     * Check if this status represents an active delivery.
     */
    public boolean isActive() {
        return this == ASSIGNED || this == EN_ROUTE_TO_PICKUP || 
               this == ARRIVED_AT_PICKUP || this == IN_TRANSIT || 
               this == ARRIVED_AT_DELIVERY;
    }
    
    /**
     * Check if this status represents a completed delivery (success or failure).
     */
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
    
    /**
     * Check if delivery can be cancelled from this status.
     */
    public boolean canBeCancelled() {
        return this == ASSIGNED || this == EN_ROUTE_TO_PICKUP || this == ARRIVED_AT_PICKUP;
    }
    
    /**
     * Check if status can transition to the given new status.
     */
    public boolean canTransitionTo(DeliveryStatus newStatus) {
        return switch (this) {
            case ASSIGNED -> newStatus == EN_ROUTE_TO_PICKUP || newStatus == CANCELLED;
            case EN_ROUTE_TO_PICKUP -> newStatus == ARRIVED_AT_PICKUP || newStatus == CANCELLED;
            case ARRIVED_AT_PICKUP -> newStatus == IN_TRANSIT || newStatus == CANCELLED;
            case IN_TRANSIT -> newStatus == ARRIVED_AT_DELIVERY || newStatus == FAILED;
            case ARRIVED_AT_DELIVERY -> newStatus == DELIVERED || newStatus == FAILED;
            case DELIVERED, CANCELLED, FAILED -> false; // Terminal states
        };
    }
}