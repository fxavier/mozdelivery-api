package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

import java.util.Map;
import java.util.Objects;


/**
 * Value object representing the result of a payment processing attempt.
 */
public record PaymentResult(
    boolean success,
    PaymentStatus status,
    String gatewayTransactionId,
    String message,
    String errorCode,
    Map<String, String> gatewayResponse
) implements ValueObject {
    
    public PaymentResult {
        Objects.requireNonNull(status, "Status cannot be null");
        
        // Create defensive copy of gateway response
        gatewayResponse = gatewayResponse != null ? Map.copyOf(gatewayResponse) : Map.of();
    }
    
    public static PaymentResult success(PaymentStatus status, String gatewayTransactionId, 
                                      String message, Map<String, String> gatewayResponse) {
        return new PaymentResult(true, status, gatewayTransactionId, message, null, gatewayResponse);
    }
    
    public static PaymentResult failure(String errorCode, String message, 
                                      Map<String, String> gatewayResponse) {
        return new PaymentResult(false, PaymentStatus.FAILED, null, message, errorCode, gatewayResponse);
    }
    
    public static PaymentResult processing(String gatewayTransactionId, String message,
                                         Map<String, String> gatewayResponse) {
        return new PaymentResult(true, PaymentStatus.PROCESSING, gatewayTransactionId, message, null, gatewayResponse);
    }
}