package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

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