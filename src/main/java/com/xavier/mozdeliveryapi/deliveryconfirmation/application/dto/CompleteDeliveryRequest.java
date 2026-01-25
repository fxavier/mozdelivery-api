package com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for completing a delivery with confirmation code.
 */
public record CompleteDeliveryRequest(
    @NotNull(message = "Order ID cannot be null")
    OrderId orderId,
    
    @NotBlank(message = "Confirmation code cannot be blank")
    String confirmationCode,
    
    @NotBlank(message = "Courier ID cannot be blank")
    String courierId,
    
    String deliveryNotes
) {
    
    public CompleteDeliveryRequest {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(confirmationCode, "Confirmation code cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        // deliveryNotes can be null
        
        if (confirmationCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Confirmation code cannot be empty");
        }
        
        if (courierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier ID cannot be empty");
        }
    }
}