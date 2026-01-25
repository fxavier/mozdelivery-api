package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Instant;
import java.util.Map;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Service for auditing DCC operations.
 * 
 * This service provides comprehensive audit logging for all DCC-related operations
 * to ensure security, compliance, and traceability.
 */
public interface DCCAuditService {
    
    /**
     * Log DCC generation event.
     */
    void logDCCGenerated(OrderId orderId, MerchantId merchantId, String code, 
                        Instant expiresAt, String sessionId, String ipAddress);
    
    /**
     * Log successful DCC validation event.
     */
    void logDCCValidated(OrderId orderId, MerchantId merchantId, String courierId, 
                        String sessionId, String ipAddress);
    
    /**
     * Log failed DCC validation attempt.
     */
    void logDCCValidationFailed(OrderId orderId, MerchantId merchantId, String courierId, 
                               String attemptedCode, int attemptNumber, int maxAttempts,
                               String sessionId, String ipAddress);
    
    /**
     * Log DCC expiration event.
     */
    void logDCCExpired(OrderId orderId, MerchantId merchantId, boolean isForced, 
                      String adminId, String reason, String sessionId, String ipAddress);
    
    /**
     * Log DCC lockout event when max attempts exceeded.
     */
    void logDCCLockout(OrderId orderId, MerchantId merchantId, String courierId, 
                      int totalAttempts, String sessionId, String ipAddress);
    
    /**
     * Log DCC resend event.
     */
    void logDCCResent(OrderId orderId, MerchantId merchantId, String newCode, 
                     String reason, String sessionId, String ipAddress);
    
    /**
     * Log suspicious DCC activity.
     */
    void logSuspiciousActivity(OrderId orderId, MerchantId merchantId, String courierId,
                              String activityType, String details, String sessionId, String ipAddress);
    
    /**
     * Check if courier has exceeded validation attempts across multiple orders.
     */
    boolean hasCourierExceededGlobalAttempts(String courierId, Instant timeWindow);
    
    /**
     * Get DCC audit trail for an order.
     */
    Map<String, Object> getDCCAuditTrail(OrderId orderId);
}