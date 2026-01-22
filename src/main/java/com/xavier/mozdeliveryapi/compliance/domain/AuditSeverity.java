package com.xavier.mozdeliveryapi.compliance.domain;

/**
 * Severity levels for audit events.
 */
public enum AuditSeverity {
    /**
     * Informational events - normal system operations.
     */
    INFO,
    
    /**
     * Warning events - potentially problematic situations.
     */
    WARNING,
    
    /**
     * Error events - error conditions that don't stop system operation.
     */
    ERROR,
    
    /**
     * Critical events - serious failures that require immediate attention.
     */
    CRITICAL;
    
    /**
     * Check if this severity requires immediate attention.
     */
    public boolean requiresImmediateAttention() {
        return this == ERROR || this == CRITICAL;
    }
    
    /**
     * Check if this severity should trigger alerts.
     */
    public boolean shouldTriggerAlert() {
        return this == CRITICAL;
    }
}