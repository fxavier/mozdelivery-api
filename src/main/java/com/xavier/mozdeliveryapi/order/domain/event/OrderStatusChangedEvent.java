package com.xavier.mozdeliveryapi.order.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;


/**
 * Domain event published when an order status changes.
 */
public record OrderStatusChangedEvent(
    OrderId orderId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderStatusChangedEvent of(OrderId orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        return new OrderStatusChangedEvent(orderId, oldStatus, newStatus, Instant.now());
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
        return "OrderStatusChanged";
    }
}