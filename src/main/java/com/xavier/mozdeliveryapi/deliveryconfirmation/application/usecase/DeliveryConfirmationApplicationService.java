package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Instant;

import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.AdminOverrideRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.AdminOverrideResult;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CompleteDeliveryRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CourierLockoutClearRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DCCStatusResponse;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DeliveryCompletionResult;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Application service interface for delivery confirmation operations.
 * This matches the interface defined in the design document.
 */
public interface DeliveryConfirmationApplicationService {
    
    /**
     * Generate a delivery confirmation code for an order.
     * 
     * @param orderId The order ID
     */
    void generateDeliveryCode(OrderId orderId);
    
    /**
     * Complete a delivery using the confirmation code.
     * 
     * @param request The delivery completion request
     * @return The result of the completion attempt
     */
    DeliveryCompletionResult completeDelivery(CompleteDeliveryRequest request);
    
    /**
     * Resend a delivery confirmation code.
     * 
     * @param orderId The order ID
     */
    void resendDeliveryCode(OrderId orderId);
    
    /**
     * Get the status of a delivery confirmation code.
     * 
     * @param orderId The order ID
     * @return The DCC status information
     */
    DCCStatusResponse getCodeStatus(OrderId orderId);
    
    /**
     * Perform admin override operations on delivery confirmation codes.
     * 
     * @param request The admin override request
     * @return The result of the override operation
     */
    AdminOverrideResult performAdminOverride(AdminOverrideRequest request);
    
    /**
     * Clear courier lockout (admin function).
     * 
     * @param request The courier lockout clear request
     * @return The result of the operation
     */
    AdminOverrideResult clearCourierLockout(CourierLockoutClearRequest request);
    
    /**
     * Get courier validation statistics for security monitoring.
     * 
     * @param courierId The courier ID
     * @param since The timestamp to get statistics since
     * @return The validation statistics
     */
    DCCSecurityService.ValidationStats getCourierValidationStats(String courierId, Instant since);
}
