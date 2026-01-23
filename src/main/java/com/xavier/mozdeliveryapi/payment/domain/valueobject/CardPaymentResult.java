package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing the result of a card payment.
 */
public record CardPaymentResult(
    boolean success,
    String transactionId,
    String authorizationCode,
    String status,
    String message,
    boolean requires3DSecure,
    String threeDSecureUrl
) implements ValueObject {
}