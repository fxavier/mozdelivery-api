package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when a Delivery Confirmation Code validation fails.
 */
public record DCCValidationFailedEvent(
    OrderId orderId,
    String courierId,
    String attemptedCode,
    int attemptNumber,
    int maxAttempts,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCValidationFailedEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(attemptedCode, "Attempted code cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
        
        if (attemptNumber < 1) {
            throw new IllegalArgumentException("Attempt number must be positive");
        }
        
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be positive");
        }
    }
    
    public static DCCValidationFailedEvent of(OrderId orderId, String courierId, String attemptedCode, 
                                            int attemptNumber, int maxAttempts) {
        return new DCCValidationFailedEvent(orderId, courierId, attemptedCode, attemptNumber, maxAttempts, Instant.now());
    }
    
    /**
     * Check if this was the final attempt.
     */
    public boolean isFinalAttempt() {
        return attemptNumber >= maxAttempts;
    }
    
    /**
     * Get remaining attempts after this failure.
     */
    public int getRemainingAttempts() {
        return Math.max(0, maxAttempts - attemptNumber);
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return orderId.value().toString();
    }
    
    @Override
    public String getEventType() {
        return "DCC_VALIDATION_FAILED";
    }
}