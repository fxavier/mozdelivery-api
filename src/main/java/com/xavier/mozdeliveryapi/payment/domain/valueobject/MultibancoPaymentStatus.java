package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;


/**
 * Value object representing Multibanco payment status.
 */
public record MultibancoPaymentStatus(
    PaymentStatus status,
    String reference,
    String message
) implements ValueObject {
}