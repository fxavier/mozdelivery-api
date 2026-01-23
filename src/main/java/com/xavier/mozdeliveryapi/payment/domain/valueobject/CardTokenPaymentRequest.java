package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;


/**
 * Value object representing a card token payment request.
 */
public record CardTokenPaymentRequest(
    String cardToken,
    Money amount,
    String description
) implements ValueObject {
}