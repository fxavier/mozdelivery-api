package com.xavier.mozdeliveryapi.shared.domain.valueobject;

import java.util.EnumSet;
import java.util.Set;

/**
 * System permissions for role-based access control.
 * Each permission represents a specific action or resource access.
 */
public enum Permission {
    // Merchant Management
    MERCHANT_CREATE("merchant:create", "Create new merchants"),
    MERCHANT_READ("merchant:read", "View merchant information"),
    MERCHANT_UPDATE("merchant:update", "Update merchant information"),
    MERCHANT_DELETE("merchant:delete", "Delete merchants"),
    MERCHANT_APPROVE("merchant:approve", "Approve merchant registrations"),
    
    // Catalog Management
    CATALOG_CREATE("catalog:create", "Create catalogs"),
    CATALOG_READ("catalog:read", "View catalogs"),
    CATALOG_UPDATE("catalog:update", "Update catalogs"),
    CATALOG_DELETE("catalog:delete", "Delete catalogs"),
    
    // Product Management
    PRODUCT_CREATE("product:create", "Create products"),
    PRODUCT_READ("product:read", "View products"),
    PRODUCT_UPDATE("product:update", "Update products"),
    PRODUCT_DELETE("product:delete", "Delete products"),
    
    // Order Management
    ORDER_CREATE("order:create", "Create orders"),
    ORDER_READ("order:read", "View orders"),
    ORDER_UPDATE("order:update", "Update order status"),
    ORDER_DELETE("order:delete", "Cancel orders"),
    ORDER_MANAGE_ALL("order:manage_all", "Manage all orders (admin)"),
    
    // Delivery Management
    DELIVERY_ASSIGN("delivery:assign", "Assign deliveries"),
    DELIVERY_READ("delivery:read", "View delivery information"),
    DELIVERY_UPDATE("delivery:update", "Update delivery status"),
    DELIVERY_COMPLETE("delivery:complete", "Complete deliveries"),
    
    // Courier Management
    COURIER_REGISTER("courier:register", "Register as courier"),
    READ_COURIER_PROFILE("courier:read_profile", "View courier profile"),
    UPDATE_COURIER_PROFILE("courier:update_profile", "Update courier profile"),
    MANAGE_COURIERS("courier:manage", "Manage courier registrations and approvals"),
    
    // Payment Management
    PAYMENT_PROCESS("payment:process", "Process payments"),
    PAYMENT_READ("payment:read", "View payment information"),
    PAYMENT_REFUND("payment:refund", "Process refunds"),
    
    // User Management
    USER_CREATE("user:create", "Create users"),
    USER_READ("user:read", "View user information"),
    USER_UPDATE("user:update", "Update user information"),
    USER_DELETE("user:delete", "Delete users"),
    
    // Analytics and Reporting
    ANALYTICS_READ("analytics:read", "View analytics"),
    ANALYTICS_EXPORT("analytics:export", "Export analytics data"),
    
    // Compliance and Audit
    AUDIT_READ("audit:read", "View audit logs"),
    COMPLIANCE_MANAGE("compliance:manage", "Manage compliance settings"),
    
    // System Administration
    SYSTEM_CONFIG("system:config", "Configure system settings"),
    SYSTEM_MONITOR("system:monitor", "Monitor system health"),
    
    // API Key Management
    MANAGE_API_KEYS("api_keys:manage", "Create, revoke, and manage API keys"),
    VIEW_API_KEYS("api_keys:view", "View API key information");
    
    private final String code;
    private final String description;
    
    Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get permissions for a specific role.
     */
    public static Set<Permission> getPermissionsForRole(UserRole role) {
        return switch (role) {
            case ADMIN -> EnumSet.allOf(Permission.class);
            
            case MERCHANT -> EnumSet.of(
                // Own merchant management
                MERCHANT_READ, MERCHANT_UPDATE,
                // Catalog and product management
                CATALOG_CREATE, CATALOG_READ, CATALOG_UPDATE, CATALOG_DELETE,
                PRODUCT_CREATE, PRODUCT_READ, PRODUCT_UPDATE, PRODUCT_DELETE,
                // Order management (own orders)
                ORDER_READ, ORDER_UPDATE,
                // Analytics (own data)
                ANALYTICS_READ, ANALYTICS_EXPORT,
                // Payment (own transactions)
                PAYMENT_READ,
                // API key management
                MANAGE_API_KEYS, VIEW_API_KEYS
            );
            
            case COURIER -> EnumSet.of(
                // Courier profile management
                READ_COURIER_PROFILE, UPDATE_COURIER_PROFILE,
                // Delivery operations
                DELIVERY_READ, DELIVERY_UPDATE, DELIVERY_COMPLETE,
                // Order information (assigned orders)
                ORDER_READ,
                // Own user profile
                USER_READ, USER_UPDATE
            );
            
            case CLIENT -> EnumSet.of(
                // Browse merchants and products
                MERCHANT_READ, CATALOG_READ, PRODUCT_READ,
                // Order management (own orders)
                ORDER_CREATE, ORDER_READ, ORDER_DELETE,
                // Payment (own transactions)
                PAYMENT_PROCESS, PAYMENT_READ,
                // Own user profile
                USER_READ, USER_UPDATE
            );
            
            case GUEST -> EnumSet.of(
                // Public browsing
                MERCHANT_READ, CATALOG_READ, PRODUCT_READ,
                // Guest checkout
                ORDER_CREATE,
                // Payment for guest orders
                PAYMENT_PROCESS,
                // Courier registration
                COURIER_REGISTER
            );
        };
    }
    
    /**
     * Check if a role has a specific permission.
     */
    public static boolean hasPermission(UserRole role, Permission permission) {
        return getPermissionsForRole(role).contains(permission);
    }
}