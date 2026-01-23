package com.xavier.mozdeliveryapi.order.application.dto;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;


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