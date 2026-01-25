package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for role-based JWT authentication converter.
 */
class RoleBasedAuthenticationTest {
    
    private final RoleBasedJwtAuthenticationConverter converter = new RoleBasedJwtAuthenticationConverter();
    
    @Test
    void shouldExtractAdminRoleAndPermissions() {
        // Given
        Jwt jwt = createJwtWithRole("ADMIN");
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should have ROLE_ADMIN
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Should have all permissions for admin
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_MERCHANT_CREATE"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_ORDER_MANAGE_ALL"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("SCOPE_merchant:create"));
    }
    
    @Test
    void shouldExtractMerchantRoleAndLimitedPermissions() {
        // Given
        Jwt jwt = createJwtWithRole("MERCHANT");
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should have ROLE_MERCHANT
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_MERCHANT"));
        
        // Should have merchant permissions
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_CATALOG_CREATE"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_PRODUCT_CREATE"));
        
        // Should NOT have admin-only permissions
        assertThat(authorities).noneMatch(auth -> auth.getAuthority().equals("PERMISSION_ORDER_MANAGE_ALL"));
        assertThat(authorities).noneMatch(auth -> auth.getAuthority().equals("PERMISSION_SYSTEM_CONFIG"));
    }
    
    @Test
    void shouldExtractCourierRoleAndDeliveryPermissions() {
        // Given
        Jwt jwt = createJwtWithRole("COURIER");
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should have ROLE_COURIER
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_COURIER"));
        
        // Should have delivery permissions
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_DELIVERY_COMPLETE"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_DELIVERY_UPDATE"));
        
        // Should NOT have merchant permissions
        assertThat(authorities).noneMatch(auth -> auth.getAuthority().equals("PERMISSION_CATALOG_CREATE"));
    }
    
    @Test
    void shouldExtractClientRoleAndOrderPermissions() {
        // Given
        Jwt jwt = createJwtWithRole("CLIENT");
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should have ROLE_CLIENT
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT"));
        
        // Should have order permissions
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_ORDER_CREATE"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_PAYMENT_PROCESS"));
        
        // Should NOT have delivery permissions
        assertThat(authorities).noneMatch(auth -> auth.getAuthority().equals("PERMISSION_DELIVERY_COMPLETE"));
    }
    
    @Test
    void shouldDefaultToGuestForUnknownRole() {
        // Given
        Jwt jwt = createJwtWithRole("UNKNOWN_ROLE");
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should have ROLE_GUEST
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_GUEST"));
        
        // Should have limited guest permissions
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_MERCHANT_READ"));
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("PERMISSION_ORDER_CREATE"));
        
        // Should NOT have authenticated user permissions
        assertThat(authorities).noneMatch(auth -> auth.getAuthority().equals("PERMISSION_USER_UPDATE"));
    }
    
    @Test
    void shouldHandleRoleFromRolesArray() {
        // Given
        Jwt jwt = createJwtWithRolesArray(List.of("MERCHANT", "CLIENT"));
        
        // When
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        
        // Then
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        
        // Should use first role in array
        assertThat(authorities).anyMatch(auth -> auth.getAuthority().equals("ROLE_MERCHANT"));
    }
    
    @Test
    void shouldExtractMerchantIdFromClaims() {
        // Given
        Jwt jwt = createJwtWithMerchantId("123e4567-e89b-12d3-a456-426614174000");
        
        // When
        String merchantId = RoleBasedJwtAuthenticationConverter.extractMerchantId(jwt);
        
        // Then
        assertThat(merchantId).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
    }
    
    @Test
    void shouldExtractUserIdFromClaims() {
        // Given
        Jwt jwt = createJwtWithUserId("user123");
        
        // When
        String userId = RoleBasedJwtAuthenticationConverter.extractUserId(jwt);
        
        // Then
        assertThat(userId).isEqualTo("user123");
    }
    
    private Jwt createJwtWithRole(String role) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("role", role)
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
    
    private Jwt createJwtWithRolesArray(List<String> roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("roles", roles)
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
    
    private Jwt createJwtWithMerchantId(String merchantId) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("role", "MERCHANT")
                .claim("merchant_id", merchantId)
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
    
    private Jwt createJwtWithUserId(String userId) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("role", "CLIENT")
                .claim("sub", userId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}