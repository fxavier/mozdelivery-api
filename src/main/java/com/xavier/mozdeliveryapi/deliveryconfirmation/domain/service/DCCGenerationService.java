package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service;

import java.time.Duration;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain service for generating secure delivery confirmation codes.
 */
public interface DCCGenerationService {
    
    /**
     * Generate a new delivery confirmation code for an order.
     * 
     * @param orderId The order ID
     * @param expirationDuration How long the code should be valid
     * @param maxAttempts Maximum validation attempts allowed
     * @return The generated DCC
     */
    DeliveryConfirmationCode generateCode(OrderId orderId, Duration expirationDuration, int maxAttempts);
    
    /**
     * Generate a new delivery confirmation code with default settings.
     * 
     * @param orderId The order ID
     * @return The generated DCC with default expiration (24 hours) and max attempts (3)
     */
    DeliveryConfirmationCode generateCode(OrderId orderId);
    
    /**
     * Regenerate a code for an existing order (e.g., when resending).
     * 
     * @param orderId The order ID
     * @param expirationDuration How long the new code should be valid
     * @param maxAttempts Maximum validation attempts allowed
     * @return The new DCC
     */
    DeliveryConfirmationCode regenerateCode(OrderId orderId, Duration expirationDuration, int maxAttempts);
}