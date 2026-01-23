package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * Value object representing a real-time tracking update.
 */
public record TrackingUpdate(
    DeliveryId deliveryId,
    DeliveryPersonId deliveryPersonId,
    Location currentLocation,
    DeliveryStatus status,
    Instant estimatedArrival,
    Duration timeToArrival,
    double progress,
    boolean isOverdue,
    String statusMessage,
    Instant timestamp
) implements ValueObject {
    
    public TrackingUpdate {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(currentLocation, "Current location cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        
        if (progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("Progress must be between 0.0 and 1.0");
        }
        // estimatedArrival, timeToArrival, and statusMessage can be null
    }
    
    public static TrackingUpdate from(Delivery delivery) {
        Objects.requireNonNull(delivery, "Delivery cannot be null");
        
        return new TrackingUpdate(
            delivery.getDeliveryId(),
            delivery.getDeliveryPersonId(),
            delivery.getCurrentLocation(),
            delivery.getStatus(),
            delivery.getEstimatedArrival(),
            delivery.getTimeToArrival(),
            delivery.getProgress(),
            delivery.isOverdue(),
            generateStatusMessage(delivery),
            Instant.now()
        );
    }
    
    private static String generateStatusMessage(Delivery delivery) {
        return switch (delivery.getStatus()) {
            case ASSIGNED -> "Delivery assigned and ready to start";
            case EN_ROUTE_TO_PICKUP -> "Delivery person is on the way to pickup location";
            case ARRIVED_AT_PICKUP -> "Delivery person has arrived at pickup location";
            case IN_TRANSIT -> "Order picked up and on the way to you";
            case ARRIVED_AT_DELIVERY -> "Delivery person has arrived at your location";
            case DELIVERED -> "Order delivered successfully";
            case CANCELLED -> "Delivery has been cancelled";
            case FAILED -> "Delivery failed - please contact support";
        };
    }
    
    /**
     * Check if this tracking update is recent (within the last minute).
     */
    public boolean isRecent() {
        return timestamp.isAfter(Instant.now().minusSeconds(60));
    }
    
    /**
     * Check if delivery is in active transit.
     */
    public boolean isInTransit() {
        return status.isActive();
    }
}