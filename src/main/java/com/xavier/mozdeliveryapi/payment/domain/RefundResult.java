package com.xavier.mozdeliveryapi.payment.domain;

import java.util.Map;
import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.RefundStatus;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of a refund processing attempt.
 */
public record RefundResult(
    boolean success,
    RefundStatus status,
    String gatewayRefundId,
    String message,
    String errorCode,
    Map<String, String> gatewayResponse
) implements ValueObject {
    
    public RefundResult {
        Objects.requireNonNull(status, "Status cannot be null");
        
        // Create defensive copy of gateway response
        gatewayResponse = gatewayResponse != null ? Map.copyOf(gatewayResponse) : Map.of();
    }
    
    public static RefundResult success(RefundStatus status, String gatewayRefundId, 
                                     String message, Map<String, String> gatewayResponse) {
        return new RefundResult(true, status, gatewayRefundId, message, null, gatewayResponse);
    }
    
    public static RefundResult failure(String errorCode, String message, 
                                     Map<String, String> gatewayResponse) {
        return new RefundResult(false, RefundStatus.FAILED, null, message, errorCode, gatewayResponse);
    }
    
    public static RefundResult processing(String gatewayRefundId, String message,
                                        Map<String, String> gatewayResponse) {
        return new RefundResult(true, RefundStatus.PROCESSING, gatewayRefundId, message, null, gatewayResponse);
    }
}