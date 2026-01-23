package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;


/**
 * Value object representing an M-Pesa B2C payment request.
 */
public record MPesaB2CRequest(
    String phoneNumber,
    Money amount,
    String commandId,
    String remarks
) implements ValueObject {
}