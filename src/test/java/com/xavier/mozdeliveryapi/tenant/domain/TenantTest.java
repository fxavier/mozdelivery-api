package com.xavier.mozdeliveryapi.tenant.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Tenant aggregate.
 */
class TenantTest {
    
    @Test
    void shouldCreateTenantWithDefaultConfiguration() {
        // Given
        TenantId tenantId = TenantId.generate();
        String tenantName = "Test Restaurant";
        Vertical vertical = Vertical.RESTAURANT;
        
        // When
        Tenant tenant = new Tenant(tenantId, tenantName, vertical);
        
        // Then
        assertThat(tenant.getTenantId()).isEqualTo(tenantId);
        assertThat(tenant.getName()).isEqualTo(tenantName);
        assertThat(tenant.getVertical()).isEqualTo(vertical);
        assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
        assertThat(tenant.canProcessOrders()).isTrue();
        assertThat(tenant.getDomainEvents()).hasSize(1);
        assertThat(tenant.getDomainEvents().get(0)).isInstanceOf(TenantCreatedEvent.class);
    }
    
    @Test
    void shouldUpdateConfiguration() {
        // Given
        Tenant tenant = new Tenant(TenantId.generate(), "Test Tenant", Vertical.GROCERY);
        TenantConfiguration newConfig = new TenantConfiguration(
            new BigDecimal("100.00"),
            new BigDecimal("500.00"),
            Duration.ofMinutes(90),
            true, true, true,
            Map.of("specialHandling", true)
        );
        
        // When
        tenant.updateConfiguration(newConfig);
        
        // Then
        assertThat(tenant.getConfiguration()).isEqualTo(newConfig);
        assertThat(tenant.getDomainEvents()).hasSize(2); // Created + ConfigurationUpdated
    }
    
    @Test
    void shouldSuspendTenant() {
        // Given
        Tenant tenant = new Tenant(TenantId.generate(), "Test Tenant", Vertical.RESTAURANT);
        String reason = "Policy violation";
        
        // When
        tenant.suspend(reason);
        
        // Then
        assertThat(tenant.getStatus()).isEqualTo(TenantStatus.SUSPENDED);
        assertThat(tenant.canProcessOrders()).isFalse();
        assertThat(tenant.canAccessSystem()).isFalse();
    }
    
    @Test
    void shouldRequirePrescriptionValidationForPharmacy() {
        // Given & When
        Tenant pharmacyTenant = new Tenant(TenantId.generate(), "Test Pharmacy", Vertical.PHARMACY);
        
        // Then
        assertThat(pharmacyTenant.requiresPrescriptionValidation()).isTrue();
        assertThat(pharmacyTenant.requiresAgeVerification()).isTrue();
    }
    
    @Test
    void shouldNotRequirePrescriptionValidationForRestaurant() {
        // Given & When
        Tenant restaurantTenant = new Tenant(TenantId.generate(), "Test Restaurant", Vertical.RESTAURANT);
        
        // Then
        assertThat(restaurantTenant.requiresPrescriptionValidation()).isFalse();
        assertThat(restaurantTenant.requiresAgeVerification()).isFalse();
    }
    
    @Test
    void shouldThrowExceptionForInvalidTenantName() {
        // Given
        TenantId tenantId = TenantId.generate();
        
        // When & Then
        assertThatThrownBy(() -> new Tenant(tenantId, "", Vertical.RESTAURANT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tenant name cannot be empty");
        
        assertThatThrownBy(() -> new Tenant(tenantId, null, Vertical.RESTAURANT))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Tenant name cannot be null");
    }
}