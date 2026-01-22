package com.xavier.mozdeliveryapi.dispatch.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a delivery person identifier.
 */
public record DeliveryPersonId(UUID value) implements ValueObject {
    
    public DeliveryPersonId {
        Objects.requireNonNull(value, "Delivery Person ID cannot be null");
    }
    
    public static DeliveryPersonId generate() {
        return new DeliveryPersonId(UUID.randomUUID());
    }
    
    public static DeliveryPersonId of(String value) {
        return new DeliveryPersonId(UUID.fromString(value));
    }
    
    public static DeliveryPersonId of(UUID value) {
        return new DeliveryPersonId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}