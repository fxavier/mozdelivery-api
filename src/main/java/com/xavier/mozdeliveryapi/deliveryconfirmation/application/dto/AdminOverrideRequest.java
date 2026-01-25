package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for admin override operations on delivery confirmation codes.
 */
public record AdminOverrideRequest(
    @NotNull(message = "Order ID cannot be null")
    OrderId orderId,
    
    @NotBlank(message = "Admin ID cannot be blank")
    String adminId,
    
    @NotBlank(message = "Reason cannot be blank")
    String reason,
    
    @NotNull(message = "Override type cannot be null")
    AdminOverrideType overrideType
) {
    
    public AdminOverrideRequest {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        Objects.requireNonNull(overrideType, "Override type cannot be null");
        
        if (adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin ID cannot be empty");
        }
        
        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
    }
    
    /**
     * Types of admin override operations.
     */
    public enum AdminOverrideType {
        FORCE_EXPIRE_CODE,
        CLEAR_COURIER_LOCKOUT,
        FORCE_COMPLETE_DELIVERY
    }
}