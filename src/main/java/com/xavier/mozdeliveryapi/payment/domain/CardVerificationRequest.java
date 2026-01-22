package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a card verification request.
 */
public record CardVerificationRequest(
    String cardNumber,
    String expiryMonth,
    String expiryYear,
    String cvv
) implements ValueObject {
}