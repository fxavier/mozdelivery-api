package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


/**
 * Domain event published when a refund is created.
 */
public record RefundCreatedEvent(
    RefundId refundId,
    PaymentId paymentId,
    Money amount,
    RefundReason reason,
    Instant timestamp
) implements DomainEvent {
    
    public static RefundCreatedEvent of(RefundId refundId, PaymentId paymentId, 
                                      Money amount, RefundReason reason) {
        return new RefundCreatedEvent(refundId, paymentId, amount, reason, Instant.now());
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
        return "RefundCreated";
    }
}