package com.xavier.mozdeliveryapi.order.application;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Request for handling payment failure.
 */
public record PaymentFailureRequest(
    String errorCode,
    String errorMessage,
    String gatewayResponse
) implements ValueObject {
    
    public PaymentFailureRequest {
        Objects.requireNonNull(errorCode, "Error code cannot be null");
        Objects.requireNonNull(errorMessage, "Error message cannot be null");
        
        if (errorCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Error code cannot be empty");
        }
        
        if (errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be empty");
        }
    }
}