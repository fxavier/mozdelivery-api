package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;

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
    
    public static PaymentResponse from(com.xavier.mozdeliveryapi.payment.domain.entity.Payment payment) {
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