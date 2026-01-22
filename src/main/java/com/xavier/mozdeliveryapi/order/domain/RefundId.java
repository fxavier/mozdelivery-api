package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a refund identifier.
 */
public record RefundId(UUID value) implements ValueObject {
    
    public RefundId {
        Objects.requireNonNull(value, "Refund ID cannot be null");
    }
    
    public static RefundId generate() {
        return new RefundId(UUID.randomUUID());
    }
    
    public static RefundId of(String value) {
        return new RefundId(UUID.fromString(value));
    }
    
    public static RefundId of(UUID value) {
        return new RefundId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}