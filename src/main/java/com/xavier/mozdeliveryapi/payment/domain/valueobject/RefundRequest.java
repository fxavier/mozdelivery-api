package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.util.Objects;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


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