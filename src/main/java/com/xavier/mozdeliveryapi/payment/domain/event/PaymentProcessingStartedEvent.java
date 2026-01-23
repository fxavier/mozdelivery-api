package com.xavier.mozdeliveryapi.payment.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;


/**
 * Domain event published when payment processing starts.
 */
public record PaymentProcessingStartedEvent(
    PaymentId paymentId,
    OrderId orderId,
    String gatewayTransactionId,
    Instant timestamp
) implements DomainEvent {
    
    public static PaymentProcessingStartedEvent of(PaymentId paymentId, OrderId orderId, 
                                                 String gatewayTransactionId) {
        return new PaymentProcessingStartedEvent(paymentId, orderId, gatewayTransactionId, Instant.now());
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
        return "PaymentProcessingStarted";
    }
}