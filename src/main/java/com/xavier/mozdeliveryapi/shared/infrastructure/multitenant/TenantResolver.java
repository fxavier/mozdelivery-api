package com.xavier.mozdeliveryapi.shared.infrastructure.multitenant;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for resolving tenant ID from HTTP requests.
 */
public interface TenantResolver {
    
    /**
     * Resolve the tenant ID from the HTTP request.
     * This could be from headers, JWT claims, subdomain, etc.
     */
    String resolveTenantId(HttpServletRequest request);
}