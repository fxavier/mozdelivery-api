package com.xavier.mozdeliveryapi.payment.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

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