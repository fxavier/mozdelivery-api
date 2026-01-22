package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of 3D Secure authentication.
 */
public record ThreeDSecureResult(
    boolean success,
    String status,
    String authorizationCode,
    String message
) implements ValueObject {
}