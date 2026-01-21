package com.xavier.mozdeliveryapi.shared.infrastructure.multitenant;

/**
 * Thread-local context for storing the current tenant ID.
 * This ensures tenant isolation across all operations.
 */
public class TenantContext {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    /**
     * Set the current tenant ID for the current thread.
     */
    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    /**
     * Get the current tenant ID for the current thread.
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * Clear the current tenant ID for the current thread.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
    
    /**
     * Check if a tenant is currently set.
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}