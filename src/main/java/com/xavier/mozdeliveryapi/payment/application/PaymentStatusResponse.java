package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;

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