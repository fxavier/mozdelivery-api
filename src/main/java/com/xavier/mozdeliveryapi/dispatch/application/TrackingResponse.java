package com.xavier.mozdeliveryapi.dispatch.application;

import java.time.Duration;
import java.time.Instant;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.TrackingUpdate;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;

/**
 * Response containing delivery tracking information.
 */
public record TrackingResponse(
    DeliveryId deliveryId,
    DeliveryPersonId deliveryPersonId,
    Location currentLocation,
    DeliveryStatus status,
    Instant estimatedArrival,
    Duration timeToArrival,
    double progress,
    boolean isOverdue,
    String statusMessage,
    Instant lastUpdated
) {
    
    public static TrackingResponse from(TrackingUpdate trackingUpdate) {
        return new TrackingResponse(
            trackingUpdate.deliveryId(),
            trackingUpdate.deliveryPersonId(),
            trackingUpdate.currentLocation(),
            trackingUpdate.status(),
            trackingUpdate.estimatedArrival(),
            trackingUpdate.timeToArrival(),
            trackingUpdate.progress(),
            trackingUpdate.isOverdue(),
            trackingUpdate.statusMessage(),
            trackingUpdate.timestamp()
        );
    }
}