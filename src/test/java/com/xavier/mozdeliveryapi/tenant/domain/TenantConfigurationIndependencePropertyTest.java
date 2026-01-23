package com.xavier.mozdeliveryapi.tenant.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantConfiguration;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tag;

/**
 * Property-based test for tenant configuration independence.
 * 
 * **Property 2: Tenant Configuration Independence**
 * **Validates: Requirements 1.2, 1.5**
 * 
 * This test verifies that configuration changes to one tenant should only affect 
 * that specific tenant's operations and not impact other tenants' behavior or data.
 */
@Tag("Feature: delivery-platform, Property 2: Tenant Configuration Independence")
class TenantConfigurationIndependencePropertyTest {
    
    /**
     * Property: Configuration changes are isolated per tenant.
     * 
     * When one tenant's configuration is updated, it should not affect
     * any other tenant's configuration or behavior.
     */
    @Property(tries = 100)
    void configurationChangesAreIsolatedPerTenant(
            @ForAll("validTenantData") TenantTestData tenant1Data,
            @ForAll("validTenantData") TenantTestData tenant2Data,
            @ForAll("validTenantConfiguration") TenantConfiguration newConfig1,
            @ForAll("validTenantConfiguration") TenantConfiguration newConfig2) {
        
        // Ensure we have two different tenants
        Assume.that(!tenant1Data.id().equals(tenant2Data.id()));
        Assume.that(!newConfig1.equals(newConfig2));
        
        // Create tenants with default configurations
        Tenant tenant1 = new Tenant(tenant1Data.id(), tenant1Data.name(), tenant1Data.vertical());
        Tenant tenant2 = new Tenant(tenant2Data.id(), tenant2Data.name(), tenant2Data.vertical());
        
        // Store original configurations
        TenantConfiguration originalConfig1 = tenant1.getConfiguration();
        TenantConfiguration originalConfig2 = tenant2.getConfiguration();
        
        // Update tenant1's configuration
        tenant1.updateConfiguration(newConfig1);
        
        // Verify tenant1's configuration changed
        assertThat(tenant1.getConfiguration()).isEqualTo(newConfig1);
        assertThat(tenant1.getConfiguration()).isNotEqualTo(originalConfig1);
        
        // Verify tenant2's configuration remained unchanged
        assertThat(tenant2.getConfiguration()).isEqualTo(originalConfig2);
        assertThat(tenant2.getConfiguration()).isNotEqualTo(newConfig1);
        
        // Update tenant2's configuration
        tenant2.updateConfiguration(newConfig2);
        
        // Verify both tenants have their respective configurations
        assertThat(tenant1.getConfiguration()).isEqualTo(newConfig1);
        assertThat(tenant2.getConfiguration()).isEqualTo(newConfig2);
        
        // Verify configurations are independent
        assertThat(tenant1.getConfiguration()).isNotEqualTo(tenant2.getConfiguration());
    }
    
    /**
     * Property: Compliance settings changes are isolated per tenant.
     * 
     * When one tenant's compliance settings are updated, it should not affect
     * any other tenant's compliance settings or behavior.
     */
    @Property(tries = 100)
    void complianceSettingsChangesAreIsolatedPerTenant(
            @ForAll("validTenantData") TenantTestData tenant1Data,
            @ForAll("validTenantData") TenantTestData tenant2Data,
            @ForAll("validComplianceSettings") ComplianceSettings newCompliance1,
            @ForAll("validComplianceSettings") ComplianceSettings newCompliance2) {
        
        // Ensure we have two different tenants
        Assume.that(!tenant1Data.id().equals(tenant2Data.id()));
        Assume.that(!newCompliance1.equals(newCompliance2));
        
        // Create tenants with default compliance settings
        Tenant tenant1 = new Tenant(tenant1Data.id(), tenant1Data.name(), tenant1Data.vertical());
        Tenant tenant2 = new Tenant(tenant2Data.id(), tenant2Data.name(), tenant2Data.vertical());
        
        // Store original compliance settings
        ComplianceSettings originalCompliance1 = tenant1.getComplianceSettings();
        ComplianceSettings originalCompliance2 = tenant2.getComplianceSettings();
        
        // Update tenant1's compliance settings
        tenant1.updateComplianceSettings(newCompliance1);
        
        // Verify tenant1's compliance settings changed
        assertThat(tenant1.getComplianceSettings()).isEqualTo(newCompliance1);
        assertThat(tenant1.getComplianceSettings()).isNotEqualTo(originalCompliance1);
        
        // Verify tenant2's compliance settings remained unchanged
        assertThat(tenant2.getComplianceSettings()).isEqualTo(originalCompliance2);
        assertThat(tenant2.getComplianceSettings()).isNotEqualTo(newCompliance1);
        
        // Update tenant2's compliance settings
        tenant2.updateComplianceSettings(newCompliance2);
        
        // Verify both tenants have their respective compliance settings
        assertThat(tenant1.getComplianceSettings()).isEqualTo(newCompliance1);
        assertThat(tenant2.getComplianceSettings()).isEqualTo(newCompliance2);
        
        // Verify compliance settings are independent
        assertThat(tenant1.getComplianceSettings()).isNotEqualTo(tenant2.getComplianceSettings());
    }
    
    /**
     * Property: Behavioral changes are isolated per tenant.
     * 
     * When one tenant's configuration affects its behavior, it should not
     * affect other tenants' behavior.
     */
    @Property(tries = 100)
    void behavioralChangesAreIsolatedPerTenant(
            @ForAll("pharmacyTenantData") TenantTestData pharmacyData,
            @ForAll("restaurantTenantData") TenantTestData restaurantData) {
        
        // Create tenants with different verticals
        Tenant pharmacyTenant = new Tenant(pharmacyData.id(), pharmacyData.name(), pharmacyData.vertical());
        Tenant restaurantTenant = new Tenant(restaurantData.id(), restaurantData.name(), restaurantData.vertical());
        
        // Store original behaviors
        boolean originalPharmacyPrescriptionReq = pharmacyTenant.requiresPrescriptionValidation();
        boolean originalPharmacyAgeReq = pharmacyTenant.requiresAgeVerification();
        boolean originalRestaurantPrescriptionReq = restaurantTenant.requiresPrescriptionValidation();
        boolean originalRestaurantAgeReq = restaurantTenant.requiresAgeVerification();
        
        // Create compliance settings that disable requirements for pharmacy
        ComplianceSettings relaxedCompliance = new ComplianceSettings(
            true, true, false,
            Map.of("prescriptionValidationRequired", false, "ageVerificationRequired", false)
        );
        
        // Update pharmacy tenant's compliance settings
        pharmacyTenant.updateComplianceSettings(relaxedCompliance);
        
        // Verify pharmacy tenant's behavior changed (but still has vertical requirements)
        assertThat(pharmacyTenant.requiresPrescriptionValidation()).isTrue(); // Vertical still requires it
        assertThat(pharmacyTenant.requiresAgeVerification()).isTrue(); // Vertical still requires it
        
        // Verify restaurant tenant's behavior remained unchanged
        assertThat(restaurantTenant.requiresPrescriptionValidation()).isEqualTo(originalRestaurantPrescriptionReq);
        assertThat(restaurantTenant.requiresAgeVerification()).isEqualTo(originalRestaurantAgeReq);
        
        // Verify tenants still have different behaviors based on their verticals
        assertThat(pharmacyTenant.requiresPrescriptionValidation()).isNotEqualTo(restaurantTenant.requiresPrescriptionValidation());
        assertThat(pharmacyTenant.requiresAgeVerification()).isNotEqualTo(restaurantTenant.requiresAgeVerification());
    }
    
    /**
     * Property: Status changes are isolated per tenant.
     * 
     * When one tenant's status changes, it should not affect other tenants' status or operations.
     */
    @Property(tries = 100)
    void statusChangesAreIsolatedPerTenant(
            @ForAll("validTenantData") TenantTestData tenant1Data,
            @ForAll("validTenantData") TenantTestData tenant2Data) {
        
        // Ensure we have two different tenants
        Assume.that(!tenant1Data.id().equals(tenant2Data.id()));
        
        // Create tenants
        Tenant tenant1 = new Tenant(tenant1Data.id(), tenant1Data.name(), tenant1Data.vertical());
        Tenant tenant2 = new Tenant(tenant2Data.id(), tenant2Data.name(), tenant2Data.vertical());
        
        // Verify both tenants start active
        assertThat(tenant1.canProcessOrders()).isTrue();
        assertThat(tenant1.canAccessSystem()).isTrue();
        assertThat(tenant2.canProcessOrders()).isTrue();
        assertThat(tenant2.canAccessSystem()).isTrue();
        
        // Suspend tenant1
        tenant1.suspend("Testing isolation");
        
        // Verify tenant1 is suspended
        assertThat(tenant1.canProcessOrders()).isFalse();
        assertThat(tenant1.canAccessSystem()).isFalse();
        
        // Verify tenant2 remains active and unaffected
        assertThat(tenant2.canProcessOrders()).isTrue();
        assertThat(tenant2.canAccessSystem()).isTrue();
        
        // Deactivate tenant2
        tenant2.deactivate();
        
        // Verify tenant2 is deactivated
        assertThat(tenant2.canProcessOrders()).isFalse();
        assertThat(tenant2.canAccessSystem()).isFalse();
        
        // Verify tenant1 status remains suspended (not affected by tenant2's change)
        assertThat(tenant1.canProcessOrders()).isFalse();
        assertThat(tenant1.canAccessSystem()).isFalse();
    }
    
    // Generators for test data
    
    @Provide
    Arbitrary<TenantTestData> validTenantData() {
        return Combinators.combine(
            Arbitraries.create(TenantId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50),
            Arbitraries.of(Vertical.class)
        ).as(TenantTestData::new);
    }
    
    @Provide
    Arbitrary<TenantTestData> pharmacyTenantData() {
        return Combinators.combine(
            Arbitraries.create(TenantId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50).map(s -> s + " Pharmacy"),
            Arbitraries.just(Vertical.PHARMACY)
        ).as(TenantTestData::new);
    }
    
    @Provide
    Arbitrary<TenantTestData> restaurantTenantData() {
        return Combinators.combine(
            Arbitraries.create(TenantId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50).map(s -> s + " Restaurant"),
            Arbitraries.just(Vertical.RESTAURANT)
        ).as(TenantTestData::new);
    }
    
    @Provide
    Arbitrary<TenantConfiguration> validTenantConfiguration() {
        return Combinators.combine(
            Arbitraries.bigDecimals().between(BigDecimal.ZERO, new BigDecimal("1000.00")),
            Arbitraries.bigDecimals().between(new BigDecimal("100.00"), new BigDecimal("5000.00")),
            Arbitraries.integers().between(15, 180).map(Duration::ofMinutes),
            Arbitraries.of(true, false),
            Arbitraries.of(true, false),
            Arbitraries.of(true, false),
            Arbitraries.maps(
                Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(20),
                Arbitraries.oneOf(
                    Arbitraries.of(true, false).map(Object.class::cast),
                    Arbitraries.strings().alpha().ofMaxLength(50).map(Object.class::cast),
                    Arbitraries.integers().between(1, 100).map(Object.class::cast)
                )
            ).ofMaxSize(5)
        ).as((deliveryFee, minOrder, maxTime, cash, card, mobile, settings) -> {
            // Ensure at least one payment method is true
            boolean acceptsCash = cash;
            boolean acceptsCard = card;
            boolean acceptsMobile = mobile;
            
            // If all are false, randomly set one to true
            if (!acceptsCash && !acceptsCard && !acceptsMobile) {
                int randomChoice = (int) (Math.random() * 3);
                switch (randomChoice) {
                    case 0: acceptsCash = true; break;
                    case 1: acceptsCard = true; break;
                    case 2: acceptsMobile = true; break;
                }
            }
            
            return new TenantConfiguration(
                deliveryFee, minOrder, maxTime,
                acceptsCash, acceptsCard, acceptsMobile,
                settings
            );
        });
    }
    
    @Provide
    Arbitrary<ComplianceSettings> validComplianceSettings() {
        return Combinators.combine(
            Arbitraries.of(true, false),
            Arbitraries.of(true, false),
            Arbitraries.of(true, false),
            Arbitraries.maps(
                Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(30),
                Arbitraries.oneOf(
                    Arbitraries.of(true, false).map(Object.class::cast),
                    Arbitraries.strings().alpha().ofMaxLength(50).map(Object.class::cast),
                    Arbitraries.integers().between(1, 100).map(Object.class::cast)
                )
            ).ofMaxSize(5)
        ).as(ComplianceSettings::new);
    }
    
    // Data class for test parameters
    record TenantTestData(TenantId id, String name, Vertical vertical) {}
}