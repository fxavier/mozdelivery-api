package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an M-Pesa B2C payment request.
 */
public record MPesaB2CRequest(
    String phoneNumber,
    Money amount,
    String commandId,
    String remarks
) implements ValueObject {
}