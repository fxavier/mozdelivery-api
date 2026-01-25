package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import com.xavier.mozdeliveryapi.shared.infra.config.RoleBasedJwtAuthenticationConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of permission service for role-based access control.
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Override
    public boolean hasPermission(Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return hasPermission(authentication, permission);
    }
    
    @Override
    public boolean hasPermissionForMerchant(Permission permission, MerchantId merchantId) {
        if (!hasPermission(permission)) {
            return false;
        }
        
        // Admin can access any merchant
        if (isAdmin()) {
            return true;
        }
        
        // Check if user belongs to the merchant
        return canAccessMerchant(merchantId);
    }
    
    @Override
    public boolean canAccessMerchant(MerchantId merchantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return canAccessMerchant(authentication, merchantId);
    }
    
    @Override
    public UserRole getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return UserRole.GUEST;
        }
        
        // Extract role from authorities
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5); // Remove "ROLE_" prefix
                try {
                    return UserRole.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    // Continue to next authority
                }
            }
        }
        
        return UserRole.GUEST;
    }
    
    @Override
    public MerchantId getCurrentUserMerchantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String merchantIdString = RoleBasedJwtAuthenticationConverter.extractMerchantId(jwt);
            if (merchantIdString != null) {
                try {
                    return MerchantId.of(UUID.fromString(merchantIdString));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format
                    return null;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return RoleBasedJwtAuthenticationConverter.extractUserId(jwt);
        }
        return null;
    }
    
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !isGuest();
    }
    
    @Override
    public boolean isAdmin() {
        return getCurrentUserRole() == UserRole.ADMIN;
    }
    
    @Override
    public boolean isMerchant() {
        return getCurrentUserRole() == UserRole.MERCHANT;
    }
    
    @Override
    public boolean isCourier() {
        return getCurrentUserRole() == UserRole.COURIER;
    }
    
    @Override
    public boolean isClient() {
        return getCurrentUserRole() == UserRole.CLIENT;
    }
    
    @Override
    public boolean isGuest() {
        return getCurrentUserRole() == UserRole.GUEST;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Permission permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Check if guest users can have this permission
            return Permission.hasPermission(UserRole.GUEST, permission);
        }
        
        // Check if user has the specific permission authority
        String permissionAuthority = "PERMISSION_" + permission.name();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(permissionAuthority)) {
                return true;
            }
        }
        
        // Fallback: check by role
        UserRole userRole = extractUserRole(authentication);
        return Permission.hasPermission(userRole, permission);
    }
    
    @Override
    public boolean canAccessMerchant(Authentication authentication, MerchantId merchantId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        UserRole userRole = extractUserRole(authentication);
        
        // Admin can access any merchant
        if (userRole == UserRole.ADMIN) {
            return true;
        }
        
        // Extract merchant ID from JWT and compare
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String userMerchantIdString = RoleBasedJwtAuthenticationConverter.extractMerchantId(jwt);
            if (userMerchantIdString != null) {
                try {
                    MerchantId userMerchantId = MerchantId.of(UUID.fromString(userMerchantIdString));
                    return userMerchantId.equals(merchantId);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        }
        
        return false;
    }
    
    private UserRole extractUserRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5);
                try {
                    return UserRole.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    // Continue to next authority
                }
            }
        }
        return UserRole.GUEST;
    }
}