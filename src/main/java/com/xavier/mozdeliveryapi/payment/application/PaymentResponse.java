package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;

import java.time.Instant;

/**
 * Response containing payment information.
 */
public record PaymentResponse(
    String id,
    String orderId,
    Money amount,
    Currency currency,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String description,
    String gatewayTransactionId,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static PaymentResponse from(com.xavier.mozdeliveryapi.payment.domain.Payment payment) {
        return new PaymentResponse(
            payment.getPaymentId().value().toString(),
            payment.getOrderId().value().toString(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getMethod(),
            payment.getStatus(),
            null, // description - not available in Payment domain model
            payment.getGatewayTransactionId(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }
}