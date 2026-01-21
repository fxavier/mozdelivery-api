package com.xavier.mozdeliveryapi.shared.infrastructure.multitenant;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Resolves tenant ID from JWT token claims.
 */
@Component
public class JwtTenantResolver implements TenantResolver {
    
    private static final String TENANT_CLAIM = "tenant_id";
    private static final String TENANT_HEADER = "X-Tenant-ID";
    
    @Override
    public String resolveTenantId(HttpServletRequest request) {
        // First try to get tenant from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String tenantId = jwt.getClaimAsString(TENANT_CLAIM);
            if (tenantId != null) {
                return tenantId;
            }
        }
        
        // Fallback to header (useful for testing and service-to-service calls)
        String tenantHeader = request.getHeader(TENANT_HEADER);
        if (tenantHeader != null && !tenantHeader.trim().isEmpty()) {
            return tenantHeader.trim();
        }
        
        return null;
    }
}