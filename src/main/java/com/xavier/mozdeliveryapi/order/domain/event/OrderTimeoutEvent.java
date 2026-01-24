package com.xavier.mozdeliveryapi.order.domain.event;

import java.time.Duration;
import java.time.Instant;

import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event published when an order times out in a status.
 */
public record OrderTimeoutEvent(
    OrderId orderId,
    OrderStatus status,
    Duration timeInStatus,
    Duration timeoutLimit,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderTimeoutEvent of(OrderId orderId, OrderStatus status, 
                                     Duration timeInStatus, Duration timeoutLimit) {
        return new OrderTimeoutEvent(orderId, status, timeInStatus, timeoutLimit, Instant.now());
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
        return "OrderTimeout";
    }
}