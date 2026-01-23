package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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