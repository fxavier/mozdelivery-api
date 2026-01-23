package com.xavier.mozdeliveryapi.order.application.dto;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;

/**
 * Request for cancelling an order.
 */
public record CancellationRequest(
    CancellationReason reason,
    String details
) implements ValueObject {
    
    public CancellationRequest {
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
    }
    
    public static CancellationRequest of(CancellationReason reason) {
        return new CancellationRequest(reason, null);
    }
    
    public static CancellationRequest of(CancellationReason reason, String details) {
        return new CancellationRequest(reason, details);
    }
}