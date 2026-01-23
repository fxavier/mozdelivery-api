package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing the result of Multibanco reference cancellation.
 */
public record MultibancoCancelResult(
    boolean success,
    String message
) implements ValueObject {
}