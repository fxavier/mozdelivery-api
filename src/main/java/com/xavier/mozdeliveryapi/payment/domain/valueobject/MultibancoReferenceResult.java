package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


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