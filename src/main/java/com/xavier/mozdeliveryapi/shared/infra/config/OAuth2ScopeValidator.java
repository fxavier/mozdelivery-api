package com.xavier.mozdeliveryapi.shared.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Service for validating OAuth2 scopes and permissions.
 */
@Service
public class OAuth2ScopeValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ScopeValidator.class);
    
    /**
     * Check if the current user has the required scope.
     * 
     * @param requiredScope the scope to check for
     * @return true if user has the scope, false otherwise
     */
    public boolean hasScope(String requiredScope) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
            .anyMatch(authority -> authority.getAuthority().equals("SCOPE_" + requiredScope));
    }
    
    /**
     * Check if the current user has any of the required scopes.
     * 
     * @param requiredScopes the scopes to check for
     * @return true if user has at least one of the scopes, false otherwise
     */
    public boolean hasAnyScope(String... requiredScopes) {
        for (String scope : requiredScopes) {
            if (hasScope(scope)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the current user has all of the required scopes.
     * 
     * @param requiredScopes the scopes to check for
     * @return true if user has all scopes, false otherwise
     */
    public boolean hasAllScopes(String... requiredScopes) {
        for (String scope : requiredScopes) {
            if (!hasScope(scope)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get all scopes for the current user.
     * 
     * @return set of scopes
     */
    public Set<String> getCurrentUserScopes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Set.of();
        }
        
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("SCOPE_"))
            .map(authority -> authority.substring(6)) // Remove "SCOPE_" prefix
            .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Get the current user ID from JWT token.
     * 
     * @return user ID or null if not available
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getSubject();
        }
        return null;
    }
    
    /**
     * Get the current tenant ID from JWT token.
     * 
     * @return tenant ID or null if not available
     */
    public String getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("tenant_id");
        }
        return null;
    }
    
    /**
     * Check if the current user has admin privileges.
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return hasAnyScope("admin", "system:admin");
    }
    
    /**
     * Check if the current user can access tenant data.
     * 
     * @param tenantId the tenant ID to check access for
     * @return true if user can access tenant data, false otherwise
     */
    public boolean canAccessTenant(String tenantId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentTenantId = getCurrentTenantId();
        return currentTenantId != null && currentTenantId.equals(tenantId);
    }
    
    /**
     * Validate scope-based access for a resource operation.
     * 
     * @param resource the resource being accessed (e.g., "order", "payment")
     * @param operation the operation being performed (e.g., "read", "write")
     * @return true if access is allowed, false otherwise
     */
    public boolean validateResourceAccess(String resource, String operation) {
        String requiredScope = resource + ":" + operation;
        boolean hasAccess = hasScope(requiredScope);
        
        if (!hasAccess) {
            logger.warn("Access denied for resource: {} operation: {} user: {}", 
                resource, operation, getCurrentUserId());
        }
        
        return hasAccess;
    }
    
    /**
     * Get user roles from JWT token.
     * 
     * @return list of user roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsStringList("roles");
        }
        return List.of();
    }
    
    /**
     * Check if the current user has a specific role.
     * 
     * @param role the role to check for
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }
}