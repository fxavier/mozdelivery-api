package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event fired when a Delivery Confirmation Code is successfully validated.
 */
public record DCCValidatedEvent(
    OrderId orderId,
    String courierId,
    Instant validatedAt,
    Instant occurredAt
) implements DomainEvent {
    
    public DCCValidatedEvent {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(validatedAt, "Validated at cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
    }
    
    public static DCCValidatedEvent of(OrderId orderId, String courierId, Instant validatedAt) {
        return new DCCValidatedEvent(orderId, courierId, validatedAt, Instant.now());
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
        return "DCC_VALIDATED";
    }
}