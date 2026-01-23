package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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