package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when a Delivery Confirmation Code is generated.
 */
public record DCCGeneratedEvent(
    OrderId orderId,
    String code,
    Instant generatedAt,
    Instant expiresAt,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCGeneratedEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(generatedAt, "Generated at cannot be null");
        Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
    }
    
    public static DCCGeneratedEvent of(OrderId orderId, String code, Instant generatedAt, Instant expiresAt) {
        return new DCCGeneratedEvent(orderId, code, generatedAt, expiresAt, Instant.now());
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
        return "DCC_GENERATED";
    }
}