package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing a 3D Secure authentication request.
 */
public record ThreeDSecureRequest(
    String transactionId,
    String paRes,
    String md
) implements ValueObject {
}