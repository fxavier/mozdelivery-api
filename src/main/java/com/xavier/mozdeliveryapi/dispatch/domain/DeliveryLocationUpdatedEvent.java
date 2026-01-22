package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a delivery location is updated.
 */
public record DeliveryLocationUpdatedEvent(
    DeliveryId deliveryId,
    Location location,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryLocationUpdatedEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryLocationUpdatedEvent of(DeliveryId deliveryId, Location location) {
        return new DeliveryLocationUpdatedEvent(deliveryId, location, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return deliveryId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DeliveryLocationUpdated";
    }
}