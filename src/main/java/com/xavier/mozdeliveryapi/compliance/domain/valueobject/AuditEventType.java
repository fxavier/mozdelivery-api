package com.xavier.mozdeliveryapi.compliance.domain.valueobject;

/**
 * Types of audit events that can be logged.
 */
public enum AuditEventType {
    // Authentication and Authorization
    USER_LOGIN("User login attempt"),
    USER_LOGOUT("User logout"),
    ACCESS_DENIED("Access denied to resource"),
    PERMISSION_GRANTED("Permission granted"),
    
    // Data Access
    DATA_READ("Data read operation"),
    DATA_WRITE("Data write operation"),
    DATA_DELETE("Data delete operation"),
    DATA_EXPORT("Data export operation"),
    
    // Order Operations
    ORDER_CREATED("Order created"),
    ORDER_UPDATED("Order updated"),
    ORDER_CANCELLED("Order cancelled"),
    ORDER_COMPLETED("Order completed"),
    
    // Payment Operations
    PAYMENT_INITIATED("Payment initiated"),
    PAYMENT_COMPLETED("Payment completed"),
    PAYMENT_FAILED("Payment failed"),
    REFUND_PROCESSED("Refund processed"),
    
    // Compliance Operations
    CONSENT_GIVEN("Consent given"),
    CONSENT_WITHDRAWN("Consent withdrawn"),
    DATA_PORTABILITY_REQUESTED("Data portability requested"),
    DATA_DELETION_REQUESTED("Data deletion requested"),
    
    // Security Events
    SUSPICIOUS_ACTIVITY("Suspicious activity detected"),
    FRAUD_ATTEMPT("Fraud attempt detected"),
    SECURITY_BREACH("Security breach detected"),
    
    // Delivery Confirmation Code Events
    DCC_GENERATED("Delivery confirmation code generated"),
    DCC_VALIDATED("Delivery confirmation code validated successfully"),
    DCC_VALIDATION_FAILED("Delivery confirmation code validation failed"),
    DCC_EXPIRED("Delivery confirmation code expired"),
    DCC_MAX_ATTEMPTS_EXCEEDED("Maximum DCC validation attempts exceeded"),
    DCC_LOCKOUT_TRIGGERED("DCC validation lockout triggered"),
    DCC_RESENT("Delivery confirmation code resent"),
    DCC_FORCE_EXPIRED("Delivery confirmation code force expired by admin"),
    
    // System Events
    SYSTEM_ERROR("System error occurred"),
    CONFIGURATION_CHANGED("System configuration changed"),
    BACKUP_CREATED("Backup created"),
    BACKUP_RESTORED("Backup restored");
    
    private final String description;
    
    AuditEventType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this event type is security-related.
     */
    public boolean isSecurityEvent() {
        return this == ACCESS_DENIED || this == SUSPICIOUS_ACTIVITY || 
               this == FRAUD_ATTEMPT || this == SECURITY_BREACH ||
               this == DCC_VALIDATION_FAILED || this == DCC_MAX_ATTEMPTS_EXCEEDED ||
               this == DCC_LOCKOUT_TRIGGERED;
    }
    
    /**
     * Check if this event type is compliance-related.
     */
    public boolean isComplianceEvent() {
        return this == CONSENT_GIVEN || this == CONSENT_WITHDRAWN ||
               this == DATA_PORTABILITY_REQUESTED || this == DATA_DELETION_REQUESTED ||
               this == DCC_GENERATED || this == DCC_VALIDATED || this == DCC_EXPIRED ||
               this == DCC_RESENT || this == DCC_FORCE_EXPIRED;
    }
}