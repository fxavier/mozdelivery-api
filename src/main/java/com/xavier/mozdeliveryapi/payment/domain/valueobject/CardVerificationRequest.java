package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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