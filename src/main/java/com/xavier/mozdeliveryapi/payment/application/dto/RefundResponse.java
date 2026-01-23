package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

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
    
    public static RefundResponse from(com.xavier.mozdeliveryapi.payment.domain.entity.Refund refund) {
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
