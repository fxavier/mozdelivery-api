package com.xavier.mozdeliveryapi.payment.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event published when a refund fails.
 */
public record RefundFailedEvent(
    RefundId refundId,
    PaymentId paymentId,
    String reason,
    Instant timestamp
) implements DomainEvent {
    
    public static RefundFailedEvent of(RefundId refundId, PaymentId paymentId, String reason) {
        return new RefundFailedEvent(refundId, paymentId, reason, Instant.now());
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
        return "RefundFailed";
    }
}