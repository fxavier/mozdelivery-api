package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;


/**
 * Value object representing an MB Way payment request.
 */
public record MBWayPaymentRequest(
    String phoneNumber,
    Money amount,
    String description
) implements ValueObject {
}