package com.xavier.mozdeliveryapi.shared.infra.config;

/**
 * Infrastructure implementation that delegates to the application TenantContext.
 * @deprecated Use com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext instead
 */
@Deprecated(forRemoval = true)
public class TenantContext {
    
    /**
     * Set the current tenant ID for the current thread.
     */
    public static void setCurrentTenant(String tenantId) {
        com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext.setCurrentTenant(tenantId);
    }
    
    /**
     * Get the current tenant ID for the current thread.
     */
    public static String getCurrentTenant() {
        return com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext.getCurrentTenant();
    }
    
    /**
     * Clear the current tenant ID for the current thread.
     */
    public static void clear() {
        com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext.clear();
    }
    
    /**
     * Check if a tenant is currently set.
     */
    public static boolean hasTenant() {
        return com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext.hasTenant();
    }
}