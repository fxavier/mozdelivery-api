package com.xavier.mozdeliveryapi.order.domain;

/**
 * Enumeration of refund statuses.
 */
public enum RefundStatus {
    PENDING("Refund is pending processing"),
    PROCESSING("Refund is being processed"),
    COMPLETED("Refund completed successfully"),
    FAILED("Refund processing failed"),
    CANCELLED("Refund was cancelled");
    
    private final String description;
    
    RefundStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if refund is in a final state.
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * Check if refund was successful.
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}