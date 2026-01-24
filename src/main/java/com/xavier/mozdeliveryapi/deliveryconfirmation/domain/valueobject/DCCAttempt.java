package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a validation attempt for a Delivery Confirmation Code.
 * 
 * This value object captures the details of each attempt to validate a DCC,
 * including who made the attempt, what code was provided, and when.
 */
public record DCCAttempt(
    String courierId,
    String attemptedCode,
    Instant attemptedAt
) {
    
    public DCCAttempt {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(attemptedCode, "Attempted code cannot be null");
        Objects.requireNonNull(attemptedAt, "Attempted at cannot be null");
        
        if (courierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier ID cannot be empty");
        }
        
        if (attemptedCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Attempted code cannot be empty");
        }
    }
    
    /**
     * Check if this attempt was made by the specified courier.
     */
    public boolean isBycourier(String courierId) {
        return this.courierId.equals(courierId);
    }
    
    /**
     * Check if this attempt was made within the specified time window.
     */
    public boolean isWithinTimeWindow(Instant start, Instant end) {
        return !attemptedAt.isBefore(start) && !attemptedAt.isAfter(end);
    }
}