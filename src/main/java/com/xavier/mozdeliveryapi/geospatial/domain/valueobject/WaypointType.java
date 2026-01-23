package com.xavier.mozdeliveryapi.geospatial.domain.valueobject;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;

/**
 * Enumeration of waypoint types in a route.
 * Defines the role of each waypoint in the delivery route.
 */
public enum WaypointType {
    
    /**
     * Starting point of the route (e.g., warehouse, restaurant).
     */
    START,
    
    /**
     * Intermediate waypoint along the route.
     */
    INTERMEDIATE,
    
    /**
     * Delivery destination where a package/order is delivered.
     */
    DELIVERY,
    
    /**
     * Final destination of the route (e.g., return to depot).
     */
    END;
    
    /**
     * Check if this waypoint type represents a stop where time is spent.
     */
    public boolean isStopPoint() {
        return this == DELIVERY || this == INTERMEDIATE;
    }
    
    /**
     * Check if this waypoint type represents a terminal point (start or end).
     */
    public boolean isTerminal() {
        return this == START || this == END;
    }
    
    /**
     * Get a human-readable description of the waypoint type.
     */
    public String getDescription() {
        return switch (this) {
            case START -> "Starting point";
            case INTERMEDIATE -> "Intermediate stop";
            case DELIVERY -> "Delivery destination";
            case END -> "End point";
        };
    }
}