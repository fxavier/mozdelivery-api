package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

/**
 * Enumeration representing types of delivery events.
 */
public enum DeliveryEventType {
    
    /**
     * Delivery was assigned to a delivery person.
     */
    ASSIGNED,
    
    /**
     * Delivery person is en route to pickup location.
     */
    EN_ROUTE_TO_PICKUP,
    
    /**
     * Delivery person arrived at pickup location.
     */
    ARRIVED_AT_PICKUP,
    
    /**
     * Order was picked up from the merchant.
     */
    PICKED_UP,
    
    /**
     * Delivery person arrived at delivery location.
     */
    ARRIVED_AT_DELIVERY,
    
    /**
     * Order was successfully delivered.
     */
    DELIVERED,
    
    /**
     * Delivery was cancelled.
     */
    CANCELLED,
    
    /**
     * Delivery failed.
     */
    FAILED,
    
    /**
     * Delivery was reassigned to another person.
     */
    REASSIGNED,
    
    /**
     * Location was updated during delivery.
     */
    LOCATION_UPDATED;
    
    /**
     * Check if this event type represents a milestone in delivery.
     */
    public boolean isMilestone() {
        return this == ASSIGNED || this == PICKED_UP || this == DELIVERED || 
               this == CANCELLED || this == FAILED;
    }
    
    /**
     * Check if this event type represents completion of delivery.
     */
    public boolean isCompletion() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
}