package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.util.Objects;

/**
 * Value object representing payment information for an order.
 */
public record PaymentInfo(
    PaymentMethod method,
    String paymentReference,
    Money amount,
    PaymentStatus status
) implements ValueObject {
    
    public PaymentInfo {
        Objects.requireNonNull(method, "Payment method cannot be null");
        Objects.requireNonNull(amount, "Payment amount cannot be null");
        Objects.requireNonNull(status, "Payment status cannot be null");
    }
    
    public static PaymentInfo pending(PaymentMethod method, Money amount) {
        return new PaymentInfo(method, null, amount, PaymentStatus.PENDING);
    }
    
    public static PaymentInfo processing(PaymentMethod method, String reference, Money amount) {
        return new PaymentInfo(method, reference, amount, PaymentStatus.PROCESSING);
    }
    
    public PaymentInfo withStatus(PaymentStatus newStatus) {
        return new PaymentInfo(method, paymentReference, amount, newStatus);
    }
    
    public PaymentInfo withReference(String reference) {
        return new PaymentInfo(method, reference, amount, status);
    }
    
    public boolean isPaid() {
        return status == PaymentStatus.COMPLETED;
    }
    
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
}