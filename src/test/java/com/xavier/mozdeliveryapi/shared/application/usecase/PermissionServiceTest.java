package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for permission service implementation.
 */
class PermissionServiceTest {
    
    private PermissionServiceImpl permissionService;
    private SecurityContext securityContext;
    
    @BeforeEach
    void setUp() {
        permissionService = new PermissionServiceImpl();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }
    
    @Test
    void shouldReturnTrueForAdminWithAnyPermission() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.MERCHANT_CREATE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.ORDER_MANAGE_ALL)).isTrue();
        assertThat(permissionService.hasPermission(Permission.SYSTEM_CONFIG)).isTrue();
    }
    
    @Test
    void shouldReturnTrueForMerchantWithCatalogPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.MERCHANT);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.CATALOG_CREATE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.PRODUCT_CREATE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.MERCHANT_READ)).isTrue();
    }
    
    @Test
    void shouldReturnFalseForMerchantWithAdminPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.MERCHANT);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.ORDER_MANAGE_ALL)).isFalse();
        assertThat(permissionService.hasPermission(Permission.SYSTEM_CONFIG)).isFalse();
        assertThat(permissionService.hasPermission(Permission.USER_DELETE)).isFalse();
    }
    
    @Test
    void shouldReturnTrueForCourierWithDeliveryPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.COURIER);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.DELIVERY_COMPLETE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.DELIVERY_UPDATE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.ORDER_READ)).isTrue();
    }
    
    @Test
    void shouldReturnFalseForCourierWithMerchantPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.COURIER);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.CATALOG_CREATE)).isFalse();
        assertThat(permissionService.hasPermission(Permission.PRODUCT_CREATE)).isFalse();
    }
    
    @Test
    void shouldReturnTrueForClientWithOrderPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.CLIENT);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.ORDER_CREATE)).isTrue();
        assertThat(permissionService.hasPermission(Permission.PAYMENT_PROCESS)).isTrue();
        assertThat(permissionService.hasPermission(Permission.MERCHANT_READ)).isTrue();
    }
    
    @Test
    void shouldReturnTrueForGuestWithPublicPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.GUEST);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.MERCHANT_READ)).isTrue();
        assertThat(permissionService.hasPermission(Permission.CATALOG_READ)).isTrue();
        assertThat(permissionService.hasPermission(Permission.ORDER_CREATE)).isTrue();
    }
    
    @Test
    void shouldReturnFalseForGuestWithAuthenticatedPermissions() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.GUEST);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.hasPermission(Permission.USER_UPDATE)).isFalse();
        assertThat(permissionService.hasPermission(Permission.ANALYTICS_READ)).isFalse();
    }
    
    @Test
    void shouldReturnCorrectUserRole() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.MERCHANT);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.getCurrentUserRole()).isEqualTo(UserRole.MERCHANT);
        assertThat(permissionService.isMerchant()).isTrue();
        assertThat(permissionService.isAdmin()).isFalse();
        assertThat(permissionService.isCourier()).isFalse();
        assertThat(permissionService.isClient()).isFalse();
        assertThat(permissionService.isGuest()).isFalse();
    }
    
    @Test
    void shouldReturnGuestForUnauthenticatedUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);
        
        // When & Then
        assertThat(permissionService.getCurrentUserRole()).isEqualTo(UserRole.GUEST);
        assertThat(permissionService.isGuest()).isTrue();
        assertThat(permissionService.isAuthenticated()).isFalse();
    }
    
    @Test
    void shouldAllowAdminToAccessAnyMerchant() {
        // Given
        Authentication auth = createAuthenticationWithRole(UserRole.ADMIN);
        when(securityContext.getAuthentication()).thenReturn(auth);
        MerchantId merchantId = MerchantId.generate();
        
        // When & Then
        assertThat(permissionService.canAccessMerchant(merchantId)).isTrue();
    }
    
    @Test
    void shouldAllowMerchantToAccessOwnMerchant() {
        // Given
        MerchantId merchantId = MerchantId.generate();
        Authentication auth = createAuthenticationWithMerchant(UserRole.MERCHANT, merchantId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.canAccessMerchant(merchantId)).isTrue();
    }
    
    @Test
    void shouldDenyMerchantAccessToOtherMerchant() {
        // Given
        MerchantId ownMerchantId = MerchantId.generate();
        MerchantId otherMerchantId = MerchantId.generate();
        Authentication auth = createAuthenticationWithMerchant(UserRole.MERCHANT, ownMerchantId);
        when(securityContext.getAuthentication()).thenReturn(auth);
        
        // When & Then
        assertThat(permissionService.canAccessMerchant(otherMerchantId)).isFalse();
    }
    
    private Authentication createAuthenticationWithRole(UserRole role) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
        
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("role", role.name())
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        
        return new JwtAuthenticationToken(jwt, authorities);
    }
    
    private Authentication createAuthenticationWithMerchant(UserRole role, MerchantId merchantId) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
        
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("role", role.name())
                .claim("merchant_id", merchantId.toString())
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        
        return new JwtAuthenticationToken(jwt, authorities);
    }
}