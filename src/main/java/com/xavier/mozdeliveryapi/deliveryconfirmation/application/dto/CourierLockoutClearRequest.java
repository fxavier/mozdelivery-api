package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for clearing courier lockout.
 */
public record CourierLockoutClearRequest(
    @NotBlank(message = "Courier ID cannot be blank")
    String courierId,
    
    @NotBlank(message = "Admin ID cannot be blank")
    String adminId,
    
    @NotBlank(message = "Reason cannot be blank")
    String reason
) {
    
    public CourierLockoutClearRequest {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        if (courierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier ID cannot be empty");
        }
        
        if (adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin ID cannot be empty");
        }
        
        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
    }
}