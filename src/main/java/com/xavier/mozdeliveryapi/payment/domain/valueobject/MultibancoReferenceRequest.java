package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.time.LocalDateTime;


/**
 * Value object representing a Multibanco reference generation request.
 */
public record MultibancoReferenceRequest(
    Money amount,
    String description,
    LocalDateTime expiryDate
) implements ValueObject {
}