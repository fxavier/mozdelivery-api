package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a card token payment request.
 */
public record CardTokenPaymentRequest(
    String cardToken,
    Money amount,
    String description
) implements ValueObject {
}