package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when a courier is locked out due to excessive DCC validation failures.
 */
public record DCCLockoutTriggeredEvent(
    OrderId orderId,
    String courierId,
    int totalAttempts,
    Instant lockoutExpiry,
    String reason,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCLockoutTriggeredEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(lockoutExpiry, "Lockout expiry cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
        
        if (totalAttempts < 1) {
            throw new IllegalArgumentException("Total attempts must be positive");
        }
    }
    
    public static DCCLockoutTriggeredEvent of(OrderId orderId, String courierId, int totalAttempts, 
                                            Instant lockoutExpiry, String reason) {
        return new DCCLockoutTriggeredEvent(orderId, courierId, totalAttempts, lockoutExpiry, reason, Instant.now());
    }
    
    /**
     * Get lockout duration in minutes.
     */
    public long getLockoutDurationMinutes() {
        return java.time.Duration.between(occurredAt, lockoutExpiry).toMinutes();
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
        return "DCC_LOCKOUT_TRIGGERED";
    }
}