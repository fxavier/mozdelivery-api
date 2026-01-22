package com.xavier.mozdeliveryapi.tenant.domain;

/**
 * Enumeration of tenant status values.
 */
public enum TenantStatus {
    ACTIVE("Active - fully operational"),
    INACTIVE("Inactive - temporarily disabled"),
    SUSPENDED("Suspended - access restricted due to policy violation");
    
    private final String description;
    
    TenantStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the tenant can process orders in this status.
     */
    public boolean canProcessOrders() {
        return this == ACTIVE;
    }
    
    /**
     * Check if the tenant can access the system in this status.
     */
    public boolean canAccessSystem() {
        return this == ACTIVE || this == INACTIVE;
    }
}