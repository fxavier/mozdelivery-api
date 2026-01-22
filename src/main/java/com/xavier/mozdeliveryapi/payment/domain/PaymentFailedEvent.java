package com.xavier.mozdeliveryapi.payment.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event published when a payment fails.
 */
public record PaymentFailedEvent(
    PaymentId paymentId,
    OrderId orderId,
    String reason,
    Instant timestamp
) implements DomainEvent {
    
    public static PaymentFailedEvent of(PaymentId paymentId, OrderId orderId, String reason) {
        return new PaymentFailedEvent(paymentId, orderId, reason, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return paymentId.toString();
    }
    
    @Override
    public String getEventType() {
        return "PaymentFailed";
    }
}