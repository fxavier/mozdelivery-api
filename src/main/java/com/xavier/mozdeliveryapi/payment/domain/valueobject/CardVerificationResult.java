package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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