package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.RefundReason;
import com.xavier.mozdeliveryapi.order.domain.RefundStatus;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.RefundId;

import java.time.Instant;

/**
 * Response containing refund information.
 */
public record RefundResponse(
    String id,
    String paymentId,
    Money amount,
    RefundReason reason,
    RefundStatus status,
    String description,
    String gatewayRefundId,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static RefundResponse from(com.xavier.mozdeliveryapi.payment.domain.Refund refund) {
        return new RefundResponse(
            refund.getRefundId().value().toString(),
            refund.getPaymentId().value().toString(),
            refund.getAmount(),
            refund.getReason(),
            refund.getStatus(),
            refund.getDescription(),
            refund.getGatewayRefundId(),
            refund.getCreatedAt(),
            refund.getUpdatedAt()
        );
    }
}