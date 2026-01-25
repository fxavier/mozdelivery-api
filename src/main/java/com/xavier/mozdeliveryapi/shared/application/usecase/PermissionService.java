package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.springframework.security.core.Authentication;

/**
 * Service for checking user permissions and access rights.
 */
public interface PermissionService {
    
    /**
     * Check if the current user has a specific permission.
     */
    boolean hasPermission(Permission permission);
    
    /**
     * Check if the current user has a specific permission for a merchant.
     */
    boolean hasPermissionForMerchant(Permission permission, MerchantId merchantId);
    
    /**
     * Check if the current user can access a specific merchant's resources.
     */
    boolean canAccessMerchant(MerchantId merchantId);
    
    /**
     * Get the current user's role.
     */
    UserRole getCurrentUserRole();
    
    /**
     * Get the current user's merchant ID (if applicable).
     */
    MerchantId getCurrentUserMerchantId();
    
    /**
     * Get the current user's ID.
     */
    String getCurrentUserId();
    
    /**
     * Check if the current user is authenticated.
     */
    boolean isAuthenticated();
    
    /**
     * Check if the current user is an admin.
     */
    boolean isAdmin();
    
    /**
     * Check if the current user is a merchant.
     */
    boolean isMerchant();
    
    /**
     * Check if the current user is a courier.
     */
    boolean isCourier();
    
    /**
     * Check if the current user is a client.
     */
    boolean isClient();
    
    /**
     * Check if the current user is a guest.
     */
    boolean isGuest();
    
    /**
     * Validate permission for authentication object.
     */
    boolean hasPermission(Authentication authentication, Permission permission);
    
    /**
     * Validate merchant access for authentication object.
     */
    boolean canAccessMerchant(Authentication authentication, MerchantId merchantId);
}