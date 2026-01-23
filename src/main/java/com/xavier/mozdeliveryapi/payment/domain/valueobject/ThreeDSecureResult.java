package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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