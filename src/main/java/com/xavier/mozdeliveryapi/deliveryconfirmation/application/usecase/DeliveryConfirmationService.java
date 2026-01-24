package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Application service for delivery confirmation code operations.
 */
public interface DeliveryConfirmationService {
    
    /**
     * Generate a delivery confirmation code for an order.
     * 
     * @param orderId The order ID
     * @return The generated DCC
     */
    DeliveryConfirmationCode generateCode(OrderId orderId);
    
    /**
     * Validate a delivery confirmation code.
     * 
     * @param orderId The order ID
     * @param code The code to validate
     * @param courierId The courier attempting validation
     * @return true if validation successful
     */
    boolean validateCode(OrderId orderId, String code, String courierId);
    
    /**
     * Resend a delivery confirmation code (generates a new one).
     * 
     * @param orderId The order ID
     * @return The new DCC
     */
    DeliveryConfirmationCode resendCode(OrderId orderId);
    
    /**
     * Get the current delivery confirmation code for an order.
     * 
     * @param orderId The order ID
     * @return The DCC if it exists
     */
    DeliveryConfirmationCode getCode(OrderId orderId);
    
    /**
     * Force expire a delivery confirmation code (admin action).
     * 
     * @param orderId The order ID
     * @param adminId The admin performing the action
     * @param reason The reason for forced expiration
     */
    void forceExpireCode(OrderId orderId, String adminId, String reason);
    
    /**
     * Handle failed validation attempt.
     * 
     * @param orderId The order ID
     * @param courierId The courier ID
     * @param attemptedCode The code that was attempted
     */
    void handleFailedAttempt(OrderId orderId, String courierId, String attemptedCode);
}