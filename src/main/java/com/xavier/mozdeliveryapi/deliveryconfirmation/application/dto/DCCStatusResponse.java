package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Response DTO for delivery confirmation code status.
 */
public record DCCStatusResponse(
    OrderId orderId,
    DCCStatus status,
    boolean isActive,
    boolean isExpired,
    Instant generatedAt,
    Instant expiresAt,
    int attemptCount,
    int maxAttempts,
    int remainingAttempts
) {
    
    public DCCStatusResponse {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(generatedAt, "Generated at cannot be null");
        Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        
        if (attemptCount < 0) {
            throw new IllegalArgumentException("Attempt count cannot be negative");
        }
        
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be positive");
        }
        
        if (remainingAttempts < 0) {
            throw new IllegalArgumentException("Remaining attempts cannot be negative");
        }
    }
}