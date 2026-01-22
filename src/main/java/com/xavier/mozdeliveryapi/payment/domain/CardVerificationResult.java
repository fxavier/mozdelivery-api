package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of card verification.
 */
public record CardVerificationResult(
    boolean valid,
    String cardType,
    String issuer,
    String message
) implements ValueObject {
}