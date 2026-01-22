package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of Multibanco reference cancellation.
 */
public record MultibancoCancelResult(
    boolean success,
    String message
) implements ValueObject {
}