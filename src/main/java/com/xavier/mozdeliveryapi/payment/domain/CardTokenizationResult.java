package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of card tokenization.
 */
public record CardTokenizationResult(
    boolean success,
    String cardToken,
    String maskedCardNumber,
    String message
) implements ValueObject {
}