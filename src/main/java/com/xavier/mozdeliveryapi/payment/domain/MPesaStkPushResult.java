package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of an M-Pesa STK Push request.
 */
public record MPesaStkPushResult(
    boolean success,
    String checkoutRequestId,
    String merchantRequestId,
    String responseCode,
    String responseDescription
) implements ValueObject {
}