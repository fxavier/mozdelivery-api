package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a delivery is cancelled.
 */
public record DeliveryCancelledEvent(
    DeliveryId deliveryId,
    OrderId orderId,
    String reason,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryCancelledEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
        // reason can be null
    }
    
    public static DeliveryCancelledEvent of(DeliveryId deliveryId, OrderId orderId, String reason) {
        return new DeliveryCancelledEvent(deliveryId, orderId, reason, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return deliveryId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DeliveryCancelled";
    }
}