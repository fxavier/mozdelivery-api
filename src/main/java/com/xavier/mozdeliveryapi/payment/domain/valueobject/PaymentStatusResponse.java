package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

import java.util.Map;
import java.util.Objects;


/**
 * Value object representing a payment status response from a gateway.
 */
public record PaymentStatusResponse(
    PaymentStatus status,
    String message,
    Map<String, String> additionalInfo
) implements ValueObject {
    
    public PaymentStatusResponse {
        Objects.requireNonNull(status, "Status cannot be null");
        
        // Create defensive copy of additional info
        additionalInfo = additionalInfo != null ? Map.copyOf(additionalInfo) : Map.of();
    }
    
    public static PaymentStatusResponse of(PaymentStatus status, String message) {
        return new PaymentStatusResponse(status, message, Map.of());
    }
    
    public static PaymentStatusResponse of(PaymentStatus status, String message, 
                                         Map<String, String> additionalInfo) {
        return new PaymentStatusResponse(status, message, additionalInfo);
    }
}