package com.xavier.mozdeliveryapi.dispatch.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a delivery identifier.
 */
public record DeliveryId(UUID value) implements ValueObject {
    
    public DeliveryId {
        Objects.requireNonNull(value, "Delivery ID cannot be null");
    }
    
    public static DeliveryId generate() {
        return new DeliveryId(UUID.randomUUID());
    }
    
    public static DeliveryId of(String value) {
        return new DeliveryId(UUID.fromString(value));
    }
    
    public static DeliveryId of(UUID value) {
        return new DeliveryId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}