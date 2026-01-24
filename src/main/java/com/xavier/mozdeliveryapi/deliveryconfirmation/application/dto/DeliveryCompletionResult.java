package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Result DTO for delivery completion attempts.
 */
public record DeliveryCompletionResult(
    OrderId orderId,
    boolean successful,
    String message,
    int remainingAttempts,
    Instant completedAt
) {
    
    public DeliveryCompletionResult {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        // completedAt can be null if not successful
        
        if (remainingAttempts < 0) {
            throw new IllegalArgumentException("Remaining attempts cannot be negative");
        }
    }
    
    public static DeliveryCompletionResult success(OrderId orderId, Instant completedAt) {
        return new DeliveryCompletionResult(
            orderId, 
            true, 
            "Delivery completed successfully", 
            0, 
            completedAt
        );
    }
    
    public static DeliveryCompletionResult failure(OrderId orderId, String message, int remainingAttempts) {
        return new DeliveryCompletionResult(
            orderId, 
            false, 
            message, 
            remainingAttempts, 
            null
        );
    }
}