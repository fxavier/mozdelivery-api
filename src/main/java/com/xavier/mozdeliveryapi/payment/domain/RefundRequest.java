package com.xavier.mozdeliveryapi.payment.domain;

import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a refund request.
 */
public record RefundRequest(
    RefundId refundId,
    PaymentId paymentId,
    Money amount,
    String reason,
    String gatewayTransactionId
) implements ValueObject {
    
    public RefundRequest {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(gatewayTransactionId, "Gateway transaction ID cannot be null");
    }
    
    public static RefundRequest of(RefundId refundId, PaymentId paymentId, Money amount, 
                                 String reason, String gatewayTransactionId) {
        return new RefundRequest(refundId, paymentId, amount, reason, gatewayTransactionId);
    }
}