package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DeliveryCompletionResult;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CompleteDeliveryRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DCCStatusResponse;

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
}