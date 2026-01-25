package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Result DTO for admin override operations.
 */
public record AdminOverrideResult(
    OrderId orderId,
    boolean successful,
    String message,
    String adminId,
    String reason,
    AdminOverrideRequest.AdminOverrideType overrideType,
    Instant performedAt
) {
    
    public AdminOverrideResult {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        Objects.requireNonNull(overrideType, "Override type cannot be null");
        Objects.requireNonNull(performedAt, "Performed at cannot be null");
    }
    
    public static AdminOverrideResult success(
            OrderId orderId, 
            String adminId, 
            String reason, 
            AdminOverrideRequest.AdminOverrideType overrideType,
            String message) {
        return new AdminOverrideResult(
            orderId, 
            true, 
            message, 
            adminId, 
            reason, 
            overrideType, 
            Instant.now()
        );
    }
    
    public static AdminOverrideResult failure(
            OrderId orderId, 
            String adminId, 
            String reason, 
            AdminOverrideRequest.AdminOverrideType overrideType,
            String message) {
        return new AdminOverrideResult(
            orderId, 
            false, 
            message, 
            adminId, 
            reason, 
            overrideType, 
            Instant.now()
        );
    }
}