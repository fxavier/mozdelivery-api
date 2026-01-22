package com.xavier.mozdeliveryapi.payment.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event published when refund processing starts.
 */
public record RefundProcessingStartedEvent(
    RefundId refundId,
    PaymentId paymentId,
    String gatewayRefundId,
    Instant timestamp
) implements DomainEvent {
    
    public static RefundProcessingStartedEvent of(RefundId refundId, PaymentId paymentId, 
                                                String gatewayRefundId) {
        return new RefundProcessingStartedEvent(refundId, paymentId, gatewayRefundId, Instant.now());
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
        return "RefundProcessingStarted";
    }
}