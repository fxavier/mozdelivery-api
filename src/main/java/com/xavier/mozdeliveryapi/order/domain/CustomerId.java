package com.xavier.mozdeliveryapi.order.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a customer identifier.
 */
public record CustomerId(UUID value) implements ValueObject {
    
    public CustomerId {
        Objects.requireNonNull(value, "Customer ID cannot be null");
    }
    
    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }
    
    public static CustomerId of(String value) {
        return new CustomerId(UUID.fromString(value));
    }
    
    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}