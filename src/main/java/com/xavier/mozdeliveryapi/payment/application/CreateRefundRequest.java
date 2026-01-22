package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.RefundReason;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;
import jakarta.validation.constraints.NotNull;

/**
 * Request to create a new refund.
 */
public record CreateRefundRequest(
    @NotNull PaymentId paymentId,
    @NotNull Money amount,
    @NotNull RefundReason reason,
    String description
) {}