package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a 3D Secure authentication request.
 */
public record ThreeDSecureRequest(
    String transactionId,
    String paRes,
    String md
) implements ValueObject {
}