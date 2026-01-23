package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing the result of an M-Pesa STK Push request.
 */
public record MPesaStkPushResult(
    boolean success,
    String checkoutRequestId,
    String merchantRequestId,
    String responseCode,
    String responseDescription
) implements ValueObject {
}