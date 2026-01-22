package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of Multibanco reference generation.
 */
public record MultibancoReferenceResult(
    boolean success,
    String reference,
    String entity,
    String message
) implements ValueObject {
}