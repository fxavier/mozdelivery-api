package com.xavier.mozdeliveryapi.payment.domain;

import java.time.LocalDateTime;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a Multibanco reference generation request.
 */
public record MultibancoReferenceRequest(
    Money amount,
    String description,
    LocalDateTime expiryDate
) implements ValueObject {
}