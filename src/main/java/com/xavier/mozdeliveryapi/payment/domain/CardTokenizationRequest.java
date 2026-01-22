package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a card tokenization request.
 */
public record CardTokenizationRequest(
    String cardNumber,
    String expiryMonth,
    String expiryYear,
    String cardholderName
) implements ValueObject {
}