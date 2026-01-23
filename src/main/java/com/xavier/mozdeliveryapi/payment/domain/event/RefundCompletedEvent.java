package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


/**
 * Domain event published when a refund is completed successfully.
 */
public record RefundCompletedEvent(
    RefundId refundId,
    PaymentId paymentId,
    Money amount,
    Instant timestamp
) implements DomainEvent {
    
    public static RefundCompletedEvent of(RefundId refundId, PaymentId paymentId, Money amount) {
        return new RefundCompletedEvent(refundId, paymentId, amount, Instant.now());
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
        return "RefundCompleted";
    }
}