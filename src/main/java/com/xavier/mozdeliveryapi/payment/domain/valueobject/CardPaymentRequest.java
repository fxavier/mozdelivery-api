package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;


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