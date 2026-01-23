package com.xavier.mozdeliveryapi.dispatch.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;

import java.time.Instant;
import java.util.Objects;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryEventType;

/**
 * Value object representing an event in the delivery lifecycle.
 */
public record DeliveryEvent(
    DeliveryEventType type,
    Location location,
    String notes,
    Instant timestamp
) implements ValueObject {
    
    public DeliveryEvent {
        Objects.requireNonNull(type, "Event type cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        // notes can be null
    }
    
    public static DeliveryEvent of(DeliveryEventType type, Location location, String notes) {
        return new DeliveryEvent(type, location, notes, Instant.now());
    }
    
    public static DeliveryEvent of(DeliveryEventType type, Location location) {
        return new DeliveryEvent(type, location, null, Instant.now());
    }
}