package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;

import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

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
