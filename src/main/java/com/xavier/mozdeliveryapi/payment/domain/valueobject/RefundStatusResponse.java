package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;

import java.util.Map;
import java.util.Objects;


/**
 * Value object representing a refund status response from a gateway.
 */
public record RefundStatusResponse(
    RefundStatus status,
    String message,
    Map<String, String> additionalInfo
) implements ValueObject {
    
    public RefundStatusResponse {
        Objects.requireNonNull(status, "Status cannot be null");
        
        // Create defensive copy of additional info
        additionalInfo = additionalInfo != null ? Map.copyOf(additionalInfo) : Map.of();
    }
    
    public static RefundStatusResponse of(RefundStatus status, String message) {
        return new RefundStatusResponse(status, message, Map.of());
    }
    
    public static RefundStatusResponse of(RefundStatus status, String message, 
                                        Map<String, String> additionalInfo) {
        return new RefundStatusResponse(status, message, additionalInfo);
    }
}