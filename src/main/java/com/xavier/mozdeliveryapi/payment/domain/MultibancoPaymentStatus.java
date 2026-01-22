package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing Multibanco payment status.
 */
public record MultibancoPaymentStatus(
    PaymentStatus status,
    String reference,
    String message
) implements ValueObject {
}