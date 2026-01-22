package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a card payment request.
 */
public record CardPaymentRequest(
    String cardNumber,
    String expiryMonth,
    String expiryYear,
    String cvv,
    String cardholderName,
    Money amount,
    String description
) implements ValueObject {
}