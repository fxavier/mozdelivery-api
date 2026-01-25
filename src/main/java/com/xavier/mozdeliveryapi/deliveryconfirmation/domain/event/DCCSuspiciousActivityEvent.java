package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when suspicious DCC validation activity is detected.
 */
public record DCCSuspiciousActivityEvent(
    OrderId orderId,
    String courierId,
    String activityType,
    String details,
    String riskLevel,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCSuspiciousActivityEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(activityType, "Activity type cannot be null");
        Objects.requireNonNull(riskLevel, "Risk level cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
        
        if (activityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity type cannot be empty");
        }
    }
    
    public static DCCSuspiciousActivityEvent of(OrderId orderId, String courierId, String activityType, 
                                              String details, String riskLevel) {
        return new DCCSuspiciousActivityEvent(orderId, courierId, activityType, details, riskLevel, Instant.now());
    }
    
    /**
     * Check if this is a high-risk activity.
     */
    public boolean isHighRisk() {
        return "HIGH".equalsIgnoreCase(riskLevel) || "CRITICAL".equalsIgnoreCase(riskLevel);
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
        return "DCC_SUSPICIOUS_ACTIVITY";
    }
}