package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.RefundStatus;
import com.xavier.mozdeliveryapi.payment.domain.RefundId;

import java.time.Instant;

/**
 * Response containing refund status information.
 */
public record RefundStatusResponse(
    String refundId,
    RefundStatus status,
    String gatewayStatus,
    String gatewayMessage,
    Instant lastChecked
) {}