package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Result of a refund operation.
 */
public record RefundResult(
    RefundId refundId,
    OrderId orderId,
    Money refundAmount,
    RefundStatus status,
    String gatewayTransactionId,
    String errorMessage,
    Instant processedAt
) implements ValueObject {
    
    public RefundResult {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(refundAmount, "Refund amount cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(processedAt, "Processed at cannot be null");
    }
    
    public static RefundResult successful(RefundId refundId, OrderId orderId, 
                                        Money refundAmount, String gatewayTransactionId) {
        return new RefundResult(
            refundId,
            orderId,
            refundAmount,
            RefundStatus.COMPLETED,
            gatewayTransactionId,
            null,
            Instant.now()
        );
    }
    
    public static RefundResult failed(RefundId refundId, OrderId orderId, 
                                    Money refundAmount, String errorMessage) {
        return new RefundResult(
            refundId,
            orderId,
            refundAmount,
            RefundStatus.FAILED,
            null,
            errorMessage,
            Instant.now()
        );
    }
    
    public boolean isSuccessful() {
        return status == RefundStatus.COMPLETED;
    }
}