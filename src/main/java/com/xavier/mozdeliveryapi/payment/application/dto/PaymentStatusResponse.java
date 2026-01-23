package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

import java.time.Instant;

/**
 * Response containing payment status information.
 */
public record PaymentStatusResponse(
    String paymentId,
    PaymentStatus status,
    String gatewayStatus,
    String gatewayMessage,
    Instant lastChecked
) {}