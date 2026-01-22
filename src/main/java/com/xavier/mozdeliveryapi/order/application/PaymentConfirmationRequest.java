package com.xavier.mozdeliveryapi.order.application;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Request for confirming a payment.
 */
public record PaymentConfirmationRequest(
    String paymentReference,
    String gatewayTransactionId
) implements ValueObject {
    
    public PaymentConfirmationRequest {
        Objects.requireNonNull(paymentReference, "Payment reference cannot be null");
        
        if (paymentReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment reference cannot be empty");
        }
    }
}