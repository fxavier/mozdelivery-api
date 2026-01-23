package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import java.util.UUID;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


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