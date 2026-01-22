package com.xavier.mozdeliveryapi.payment.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a refund identifier.
 */
public record RefundId(UUID value) implements ValueObject {
    
    public RefundId {
        Objects.requireNonNull(value, "Refund ID value cannot be null");
    }
    
    public static RefundId generate() {
        return new RefundId(UUID.randomUUID());
    }
    
    public static RefundId of(String value) {
        return new RefundId(UUID.fromString(value));
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}