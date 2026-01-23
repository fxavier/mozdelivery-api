package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import java.util.UUID;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;


/**
 * Value object representing a payment identifier.
 */
public record PaymentId(UUID value) implements ValueObject {
    
    public PaymentId {
        Objects.requireNonNull(value, "Payment ID value cannot be null");
    }
    
    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }
    
    public static PaymentId of(String value) {
        return new PaymentId(UUID.fromString(value));
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}