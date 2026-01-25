package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Service for DCC security operations.
 * 
 * This service handles security aspects of DCC validation including
 * rate limiting, lockout mechanisms, and suspicious activity detection.
 */
public interface DCCSecurityService {
    
    /**
     * Check if a courier is currently locked out from DCC validation.
     */
    boolean isCourierLockedOut(String courierId);
    
    /**
     * Check if a courier can attempt DCC validation for a specific order.
     */
    boolean canCourierAttemptValidation(String courierId, OrderId orderId);
    
    /**
     * Apply lockout to a courier after exceeding maximum attempts.
     */
    void applyCourierLockout(String courierId, OrderId orderId, int attemptCount);
    
    /**
     * Check for suspicious validation patterns.
     */
    boolean detectSuspiciousActivity(String courierId, OrderId orderId, String attemptedCode);
    
    /**
     * Get remaining lockout time for a courier.
     */
    long getRemainingLockoutTime(String courierId);
    
    /**
     * Clear lockout for a courier (admin action).
     */
    void clearCourierLockout(String courierId, String adminId, String reason);
    
    /**
     * Check if validation rate limit is exceeded for a courier.
     */
    boolean isRateLimitExceeded(String courierId);
    
    /**
     * Record a validation attempt for rate limiting.
     */
    void recordValidationAttempt(String courierId, OrderId orderId, boolean successful);
    
    /**
     * Get validation attempt statistics for a courier.
     */
    ValidationStats getCourierValidationStats(String courierId, Instant since);
    
    /**
     * Statistics for courier validation attempts.
     */
    record ValidationStats(
        int totalAttempts,
        int successfulAttempts,
        int failedAttempts,
        int uniqueOrders,
        Instant firstAttempt,
        Instant lastAttempt,
        boolean isLockedOut,
        long lockoutRemainingSeconds
    ) {}
}