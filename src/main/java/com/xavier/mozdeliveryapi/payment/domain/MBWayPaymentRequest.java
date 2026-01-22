package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an MB Way payment request.
 */
public record MBWayPaymentRequest(
    String phoneNumber,
    Money amount,
    String description
) implements ValueObject {
}