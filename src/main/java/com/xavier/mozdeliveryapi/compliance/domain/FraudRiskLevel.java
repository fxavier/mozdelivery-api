package com.xavier.mozdeliveryapi.compliance.domain;

/**
 * Risk levels for fraud detection.
 */
public enum FraudRiskLevel {
    /**
     * Low risk - normal activity patterns.
     */
    LOW,
    
    /**
     * Medium risk - some suspicious indicators.
     */
    MEDIUM,
    
    /**
     * High risk - multiple suspicious indicators.
     */
    HIGH,
    
    /**
     * Critical risk - clear fraud indicators.
     */
    CRITICAL;
    
    /**
     * Check if this risk level requires manual review.
     */
    public boolean requiresManualReview() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Check if this risk level should block the transaction.
     */
    public boolean shouldBlockTransaction() {
        return this == CRITICAL;
    }
    
    /**
     * Check if this risk level should trigger alerts.
     */
    public boolean shouldTriggerAlert() {
        return this == HIGH || this == CRITICAL;
    }
}