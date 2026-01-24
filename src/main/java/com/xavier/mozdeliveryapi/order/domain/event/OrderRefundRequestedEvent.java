package com.xavier.mozdeliveryapi.order.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event published when an order refund is requested.
 */
public record OrderRefundRequestedEvent(
    OrderId orderId,
    Money refundAmount,
    String reason,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderRefundRequestedEvent of(OrderId orderId, Money refundAmount, String reason) {
        return new OrderRefundRequestedEvent(orderId, refundAmount, reason, Instant.now());
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
        return "OrderRefundRequested";
    }
}