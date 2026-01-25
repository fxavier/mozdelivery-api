package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for common security operations.
 */
@Component
public class SecurityUtils {
    
    private final PermissionService permissionService;
    
    public SecurityUtils(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Ensure the current user can access the specified merchant.
     * Throws AccessDeniedException if access is denied.
     */
    public void ensureMerchantAccess(MerchantId merchantId) {
        if (!permissionService.canAccessMerchant(merchantId)) {
            throw new AccessDeniedException("Access denied to merchant: " + merchantId);
        }
    }
    
    /**
     * Ensure the current user has admin privileges.
     * Throws AccessDeniedException if not admin.
     */
    public void ensureAdmin() {
        if (!permissionService.isAdmin()) {
            throw new AccessDeniedException("Admin privileges required");
        }
    }
    
    /**
     * Ensure the current user has one of the specified roles.
     * Throws AccessDeniedException if role check fails.
     */
    public void ensureRole(UserRole... allowedRoles) {
        UserRole currentRole = permissionService.getCurrentUserRole();
        for (UserRole allowedRole : allowedRoles) {
            if (currentRole == allowedRole) {
                return;
            }
        }
        throw new AccessDeniedException("Insufficient role. Required: " + 
            java.util.Arrays.toString(allowedRoles) + ", Current: " + currentRole);
    }
    
    /**
     * Ensure the current user is authenticated.
     * Throws AccessDeniedException if not authenticated.
     */
    public void ensureAuthenticated() {
        if (!permissionService.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
    }
    
    /**
     * Get the current merchant ID, ensuring the user has merchant access.
     * Returns null if user is admin (can access any merchant).
     */
    public MerchantId getCurrentMerchantIdOrNull() {
        if (permissionService.isAdmin()) {
            return null; // Admin can access any merchant
        }
        return permissionService.getCurrentUserMerchantId();
    }
    
    /**
     * Get the current merchant ID, throwing exception if not available.
     */
    public MerchantId requireCurrentMerchantId() {
        MerchantId merchantId = permissionService.getCurrentUserMerchantId();
        if (merchantId == null && !permissionService.isAdmin()) {
            throw new AccessDeniedException("Merchant context required");
        }
        return merchantId;
    }
    
    /**
     * Check if the current user can access resources for the given merchant.
     * Admins can access any merchant, others only their own.
     */
    public boolean canAccessMerchantResources(MerchantId merchantId) {
        return permissionService.canAccessMerchant(merchantId);
    }
    
    /**
     * Get current authentication or throw exception if not authenticated.
     */
    public Authentication requireAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return auth;
    }
    
    /**
     * Get the current user ID from the security context.
     */
    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return auth.getName();
    }
    
    /**
     * Get the current merchant ID from the security context.
     * For API key authentication, this returns the merchant ID from the API key.
     * For JWT authentication, this returns the merchant ID from the user's context.
     */
    public static String getCurrentMerchantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        
        // For API key authentication
        if (auth.getDetails() instanceof com.xavier.mozdeliveryapi.shared.infra.config.ApiKeyAuthenticationFilter.ApiKeyAuthenticationDetails apiKeyDetails) {
            return apiKeyDetails.getMerchantId();
        }
        
        // For JWT authentication, we need to extract merchant ID from the token or user context
        // This is a simplified implementation - in production you might need more sophisticated logic
        if (auth instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
            org.springframework.security.oauth2.jwt.Jwt jwt = jwtAuth.getToken();
            String merchantId = jwt.getClaimAsString("merchant_id");
            if (merchantId != null) {
                return merchantId;
            }
        }
        
        // Fallback - if no merchant context is available, throw exception
        throw new AccessDeniedException("Merchant context required");
    }
}