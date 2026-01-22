package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of an MB Way payment.
 */
public record MBWayPaymentResult(
    boolean success,
    String transactionId,
    String status,
    String message
) implements ValueObject {
}