package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;
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