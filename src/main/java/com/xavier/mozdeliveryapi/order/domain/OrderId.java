package com.xavier.mozdeliveryapi.order.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an order identifier.
 */
public record OrderId(UUID value) implements ValueObject {
    
    public OrderId {
        Objects.requireNonNull(value, "Order ID cannot be null");
    }
    
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
    
    public static OrderId of(String value) {
        return new OrderId(UUID.fromString(value));
    }
    
    public static OrderId of(UUID value) {
        return new OrderId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}