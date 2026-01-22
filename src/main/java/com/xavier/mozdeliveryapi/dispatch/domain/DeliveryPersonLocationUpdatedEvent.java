package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a delivery person's location is updated.
 */
public record DeliveryPersonLocationUpdatedEvent(
    DeliveryPersonId deliveryPersonId,
    Location location,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryPersonLocationUpdatedEvent {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryPersonLocationUpdatedEvent of(DeliveryPersonId deliveryPersonId, 
                                                       Location location) {
        return new DeliveryPersonLocationUpdatedEvent(deliveryPersonId, location, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return deliveryPersonId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DeliveryPersonLocationUpdated";
    }
}