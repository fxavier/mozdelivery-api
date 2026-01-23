package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

/**
 * Enumeration of merchant status values.
 */
public enum MerchantStatus {
    PENDING("Pending - awaiting approval"),
    ACTIVE("Active - fully operational"),
    INACTIVE("Inactive - temporarily disabled"),
    REJECTED("Rejected - application denied"),
    SUSPENDED("Suspended - access restricted due to policy violation");
    
    private final String description;
    
    MerchantStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the merchant can process orders in this status.
     */
    public boolean canProcessOrders() {
        return this == ACTIVE;
    }
    
    /**
     * Check if the merchant can access the system in this status.
     */
    public boolean canAccessSystem() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * Check if the merchant is approved for operations.
     */
    public boolean isApproved() {
        return this == ACTIVE || this == INACTIVE || this == SUSPENDED;
    }
}