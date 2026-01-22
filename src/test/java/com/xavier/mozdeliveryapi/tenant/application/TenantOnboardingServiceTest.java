package com.xavier.mozdeliveryapi.tenant.application;

import com.xavier.mozdeliveryapi.shared.application.DomainEventPublisher;
import com.xavier.mozdeliveryapi.tenant.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TenantOnboardingService.
 */
@ExtendWith(MockitoExtension.class)
class TenantOnboardingServiceTest {
    
    @Mock
    private TenantRepository tenantRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private TenantOnboardingService service;
    
    @BeforeEach
    void setUp() {
        service = new TenantOnboardingService(tenantRepository, eventPublisher);
    }
    
    @Test
    void shouldOnboardTenantSuccessfully() {
        // Given
        TenantOnboardingRequest request = new TenantOnboardingRequest(
            "Test Restaurant",
            Vertical.RESTAURANT,
            "test@restaurant.com",
            "+258123456789",
            "123 Main St, Maputo"
        );
        
        when(tenantRepository.existsByName(anyString())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        TenantOnboardingResponse response = service.onboardTenant(request);
        
        // Then
        assertThat(response.status()).isEqualTo("SUCCESS");
        assertThat(response.tenantName()).isEqualTo("Test Restaurant");
        assertThat(response.vertical()).isEqualTo(Vertical.RESTAURANT);
        assertThat(response.tenantId()).isNotNull();
        
        verify(tenantRepository).existsByName("Test Restaurant");
        verify(tenantRepository).save(any(Tenant.class));
        verify(eventPublisher, atLeastOnce()).publish(any());
    }
    
    @Test
    void shouldFailOnboardingWhenTenantNameExists() {
        // Given
        TenantOnboardingRequest request = new TenantOnboardingRequest(
            "Existing Restaurant",
            Vertical.RESTAURANT,
            "test@restaurant.com",
            "+258123456789",
            "123 Main St, Maputo"
        );
        
        when(tenantRepository.existsByName("Existing Restaurant")).thenReturn(true);
        
        // When
        TenantOnboardingResponse response = service.onboardTenant(request);
        
        // Then
        assertThat(response.status()).isEqualTo("FAILURE");
        assertThat(response.message()).contains("Tenant name already exists");
        
        verify(tenantRepository).existsByName("Existing Restaurant");
        verify(tenantRepository, never()).save(any(Tenant.class));
        verify(eventPublisher, never()).publish(any());
    }
    
    @Test
    void shouldApplyPharmacySpecificRulesOnOnboarding() {
        // Given
        TenantOnboardingRequest request = new TenantOnboardingRequest(
            "Test Pharmacy",
            Vertical.PHARMACY,
            "test@pharmacy.com",
            "+258123456789",
            "456 Health St, Maputo"
        );
        
        when(tenantRepository.existsByName(anyString())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant tenant = invocation.getArgument(0);
            // Verify pharmacy-specific rules are applied
            assertThat(tenant.getVertical()).isEqualTo(Vertical.PHARMACY);
            assertThat(tenant.requiresPrescriptionValidation()).isTrue();
            assertThat(tenant.requiresAgeVerification()).isTrue();
            assertThat(tenant.getConfiguration().acceptsCashPayments()).isFalse(); // No cash for pharmacy
            return tenant;
        });
        
        // When
        TenantOnboardingResponse response = service.onboardTenant(request);
        
        // Then
        assertThat(response.status()).isEqualTo("SUCCESS");
        assertThat(response.vertical()).isEqualTo(Vertical.PHARMACY);
        
        verify(tenantRepository).save(any(Tenant.class));
    }
    
    @Test
    void shouldGetOnboardingStats() {
        // Given
        when(tenantRepository.countByStatus(TenantStatus.ACTIVE)).thenReturn(10L);
        when(tenantRepository.countByStatus(TenantStatus.INACTIVE)).thenReturn(2L);
        when(tenantRepository.countByStatus(TenantStatus.SUSPENDED)).thenReturn(1L);
        when(tenantRepository.countByVertical(Vertical.RESTAURANT)).thenReturn(5L);
        when(tenantRepository.countByVertical(Vertical.GROCERY)).thenReturn(3L);
        when(tenantRepository.countByVertical(Vertical.PHARMACY)).thenReturn(2L);
        
        // When
        TenantOnboardingStats stats = service.getOnboardingStats();
        
        // Then
        assertThat(stats.totalTenants()).isEqualTo(13L);
        assertThat(stats.activeTenants()).isEqualTo(10L);
        assertThat(stats.restaurantCount()).isEqualTo(5L);
        assertThat(stats.groceryCount()).isEqualTo(3L);
        assertThat(stats.pharmacyCount()).isEqualTo(2L);
        assertThat(stats.getActivePercentage()).isCloseTo(76.92, within(0.01));
    }
}