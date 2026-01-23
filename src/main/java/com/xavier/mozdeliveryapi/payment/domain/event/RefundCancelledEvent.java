package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


/**
 * Domain event published when a refund is cancelled.
 */
public record RefundCancelledEvent(
    RefundId refundId,
    PaymentId paymentId,
    Instant timestamp
) implements DomainEvent {
    
    public static RefundCancelledEvent of(RefundId refundId, PaymentId paymentId) {
        return new RefundCancelledEvent(refundId, paymentId, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return refundId.toString();
    }
    
    @Override
    public String getEventType() {
        return "RefundCancelled";
    }
}