package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;


/**
 * Domain event published when a payment is created.
 */
public record PaymentCreatedEvent(
    PaymentId paymentId,
    OrderId orderId,
    Money amount,
    PaymentMethod method,
    Instant timestamp
) implements DomainEvent {
    
    public static PaymentCreatedEvent of(PaymentId paymentId, OrderId orderId, 
                                       Money amount, PaymentMethod method) {
        return new PaymentCreatedEvent(paymentId, orderId, amount, method, Instant.now());
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
        return "PaymentCreated";
    }
}