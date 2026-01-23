package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;


/**
 * Domain event published when a payment is completed successfully.
 */
public record PaymentCompletedEvent(
    PaymentId paymentId,
    OrderId orderId,
    Money amount,
    Instant timestamp
) implements DomainEvent {
    
    public static PaymentCompletedEvent of(PaymentId paymentId, OrderId orderId, Money amount) {
        return new PaymentCompletedEvent(paymentId, orderId, amount, Instant.now());
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
        return "PaymentCompleted";
    }
}