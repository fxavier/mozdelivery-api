package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when a Delivery Confirmation Code expires.
 */
public record DCCExpiredEvent(
    OrderId orderId,
    String adminId,
    String reason,
    Instant expiredAt,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCExpiredEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(expiredAt, "Expired at cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
        // adminId and reason can be null for natural expiration
    }
    
    public static DCCExpiredEvent of(OrderId orderId, Instant expiredAt) {
        return new DCCExpiredEvent(orderId, null, null, expiredAt, Instant.now());
    }
    
    public static DCCExpiredEvent ofForced(OrderId orderId, String adminId, String reason, Instant expiredAt) {
        return new DCCExpiredEvent(orderId, adminId, reason, expiredAt, Instant.now());
    }
    
    /**
     * Check if this was a forced expiration by an admin.
     */
    public boolean isForcedExpiration() {
        return adminId != null;
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
        return "DCC_EXPIRED";
    }
}