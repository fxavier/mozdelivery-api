package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;


/**
 * Domain event published when a payment is cancelled.
 */
public record PaymentCancelledEvent(
    PaymentId paymentId,
    OrderId orderId,
    Instant timestamp
) implements DomainEvent {
    
    public static PaymentCancelledEvent of(PaymentId paymentId, OrderId orderId) {
        return new PaymentCancelledEvent(paymentId, orderId, Instant.now());
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
        return "PaymentCancelled";
    }
}