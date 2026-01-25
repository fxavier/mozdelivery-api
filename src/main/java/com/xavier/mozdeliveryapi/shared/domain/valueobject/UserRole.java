package com.xavier.mozdeliveryapi.shared.domain.valueobject;

/**
 * User roles in the multi-merchant delivery platform.
 * Each role has specific permissions and access levels.
 */
public enum UserRole {
    /**
     * Platform administrator with full system access.
     */
    ADMIN("admin", "Platform Administrator"),
    
    /**
     * Business owner/manager with access to their merchant operations.
     */
    MERCHANT("merchant", "Merchant/Business Owner"),
    
    /**
     * Delivery person with access to delivery operations.
     */
    COURIER("courier", "Delivery Person"),
    
    /**
     * Registered customer with access to ordering and tracking.
     */
    CLIENT("client", "Registered Customer"),
    
    /**
     * Anonymous user with limited access to browsing and guest checkout.
     */
    GUEST("guest", "Guest User");
    
    private final String code;
    private final String displayName;
    
    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get role from code string.
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }
    
    /**
     * Check if this role has administrative privileges.
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Check if this role can manage merchant operations.
     */
    public boolean canManageMerchant() {
        return this == ADMIN || this == MERCHANT;
    }
    
    /**
     * Check if this role can perform delivery operations.
     */
    public boolean canDeliverOrders() {
        return this == ADMIN || this == COURIER;
    }
    
    /**
     * Check if this role can place orders.
     */
    public boolean canPlaceOrders() {
        return this == ADMIN || this == CLIENT || this == GUEST;
    }
    
    /**
     * Check if this role requires authentication.
     */
    public boolean requiresAuthentication() {
        return this != GUEST;
    }
}