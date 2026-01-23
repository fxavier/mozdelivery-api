package com.xavier.mozdeliveryapi.order.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;


/**
 * Domain event published when an order is cancelled.
 */
public record OrderCancelledEvent(
    OrderId orderId,
    CancellationReason reason,
    String details,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderCancelledEvent of(OrderId orderId, CancellationReason reason, String details) {
        return new OrderCancelledEvent(orderId, reason, details, Instant.now());
    }
    
    public static OrderCancelledEvent of(OrderId orderId, CancellationReason reason) {
        return new OrderCancelledEvent(orderId, reason, null, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
    
    @Override
    public String getEventType() {
        return "OrderCancelled";
    }
}