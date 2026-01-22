package com.xavier.mozdeliveryapi.order.domain;

/**
 * Enumeration of payment statuses.
 */
public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment was cancelled"),
    REFUNDED("Payment was refunded");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if payment is in a final state.
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
    
    /**
     * Check if payment was successful.
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}