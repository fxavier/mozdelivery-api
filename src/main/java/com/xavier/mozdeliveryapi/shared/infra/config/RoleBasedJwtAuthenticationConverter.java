package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT authentication converter that extracts roles and permissions from JWT claims
 * and converts them to Spring Security authorities.
 */
@Component
public class RoleBasedJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_CLAIM = "role";
    private static final String SCOPE_CLAIM = "scope";
    private static final String SCP_CLAIM = "scp";
    private static final String MERCHANT_ID_CLAIM = "merchant_id";
    private static final String USER_ID_CLAIM = "sub";
    
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }
    
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Extract user role
        UserRole userRole = extractUserRole(jwt);
        if (userRole != null) {
            // Add role authority
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
            
            // Add permission authorities based on role
            Set<Permission> permissions = Permission.getPermissionsForRole(userRole);
            for (Permission permission : permissions) {
                authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.name()));
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + permission.getCode()));
            }
        }
        
        // Extract explicit scopes from JWT (for backward compatibility)
        extractScopes(jwt, authorities);
        
        return authorities;
    }
    
    private UserRole extractUserRole(Jwt jwt) {
        // Try multiple claim names for role
        String roleString = null;
        
        // Check for roles array (first element)
        List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
        if (roles != null && !roles.isEmpty()) {
            roleString = roles.get(0);
        }
        
        // Check for single role claim
        if (roleString == null) {
            roleString = jwt.getClaimAsString(ROLE_CLAIM);
        }
        
        if (roleString != null) {
            try {
                // Handle both uppercase and lowercase role names
                return UserRole.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Try to find by code
                try {
                    return UserRole.fromCode(roleString.toLowerCase());
                } catch (IllegalArgumentException ex) {
                    // Log warning and default to GUEST
                    System.err.println("Unknown role in JWT: " + roleString + ", defaulting to GUEST");
                    return UserRole.GUEST;
                }
            }
        }
        
        // Default to GUEST if no role found
        return UserRole.GUEST;
    }
    
    private void extractScopes(Jwt jwt, Set<GrantedAuthority> authorities) {
        // Extract scopes from 'scope' claim
        List<String> scopes = jwt.getClaimAsStringList(SCOPE_CLAIM);
        if (scopes != null) {
            for (String scope : scopes) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
            }
        }
        
        // Extract scopes from 'scp' claim (alternative scope claim name)
        List<String> scp = jwt.getClaimAsStringList(SCP_CLAIM);
        if (scp != null) {
            for (String scope : scp) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
            }
        }
    }
    
    /**
     * Extract merchant ID from JWT claims.
     */
    public static String extractMerchantId(Jwt jwt) {
        return jwt.getClaimAsString(MERCHANT_ID_CLAIM);
    }
    
    /**
     * Extract user ID from JWT claims.
     */
    public static String extractUserId(Jwt jwt) {
        return jwt.getClaimAsString(USER_ID_CLAIM);
    }
}