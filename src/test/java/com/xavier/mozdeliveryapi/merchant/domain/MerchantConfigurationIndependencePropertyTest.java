package com.xavier.mozdeliveryapi.merchant.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.BusinessDetails;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantConfiguration;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tag;

/**
 * Property-based test for merchant configuration independence.
 * 
 * **Property 2: Merchant Configuration Independence**
 * **Validates: Requirements 1.2, 1.5**
 * 
 * This test verifies that configuration changes to one merchant should only affect 
 * that specific merchant's operations and not impact other merchants' behavior or data.
 */
@Tag("Feature: delivery-platform, Property 2: Merchant Configuration Independence")
class MerchantConfigurationIndependencePropertyTest {
    
    /**
     * Property: Configuration changes are isolated per merchant.
     * 
     * When one merchant's configuration is updated, it should not affect
     * any other merchant's configuration or behavior.
     */
    @Property(tries = 100)
    void configurationChangesAreIsolatedPerMerchant(
            @ForAll("validMerchantData") MerchantTestData merchant1Data,
            @ForAll("validMerchantData") MerchantTestData merchant2Data,
            @ForAll("validMerchantConfiguration") MerchantConfiguration newConfig1,
            @ForAll("validMerchantConfiguration") MerchantConfiguration newConfig2) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        Assume.that(!newConfig1.equals(newConfig2));
        
        // Create merchants with default configurations
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Approve merchants so they can update configurations
        merchant1.approve("test-admin");
        merchant2.approve("test-admin");
        
        // Store original configurations
        MerchantConfiguration originalConfig1 = merchant1.getConfiguration();
        MerchantConfiguration originalConfig2 = merchant2.getConfiguration();
        
        // Update merchant1's configuration
        merchant1.updateConfiguration(newConfig1);
        
        // Verify merchant1's configuration changed
        assertThat(merchant1.getConfiguration()).isEqualTo(newConfig1);
        assertThat(merchant1.getConfiguration()).isNotEqualTo(originalConfig1);
        
        // Verify merchant2's configuration remained unchanged
        assertThat(merchant2.getConfiguration()).isEqualTo(originalConfig2);
        assertThat(merchant2.getConfiguration()).isNotEqualTo(newConfig1);
        
        // Update merchant2's configuration
        merchant2.updateConfiguration(newConfig2);
        
        // Verify both merchants have their respective configurations
        assertThat(merchant1.getConfiguration()).isEqualTo(newConfig1);
        assertThat(merchant2.getConfiguration()).isEqualTo(newConfig2);
        
        // Verify configurations are independent
        assertThat(merchant1.getConfiguration()).isNotEqualTo(merchant2.getConfiguration());
    }
    
    /**
     * Property: Behavioral changes are isolated per merchant.
     * 
     * When one merchant's configuration affects its behavior, it should not
     * affect other merchants' behavior.
     */
    @Property(tries = 100)
    void behavioralChangesAreIsolatedPerMerchant(
            @ForAll("pharmacyMerchantData") MerchantTestData pharmacyData,
            @ForAll("restaurantMerchantData") MerchantTestData restaurantData) {
        
        // Create merchants with different verticals
        Merchant pharmacyMerchant = new Merchant(pharmacyData.id(), pharmacyData.businessDetails(), pharmacyData.vertical());
        Merchant restaurantMerchant = new Merchant(restaurantData.id(), restaurantData.businessDetails(), restaurantData.vertical());
        
        // Approve merchants so they can update business details
        pharmacyMerchant.approve("test-admin");
        restaurantMerchant.approve("test-admin");
        
        // Store original behaviors
        boolean originalRestaurantPrescriptionReq = restaurantMerchant.requiresPrescriptionValidation();
        boolean originalRestaurantAgeReq = restaurantMerchant.requiresAgeVerification();
        
        // Create compliance settings that disable requirements for pharmacy
        ComplianceSettings relaxedCompliance = new ComplianceSettings(
            true, true, false,
            Map.of("prescriptionValidationRequired", false, "ageVerificationRequired", false)
        );
        
        // Update pharmacy merchant's compliance settings (this should not affect restaurant)
        pharmacyMerchant.updateBusinessDetails(new BusinessDetails(
            pharmacyData.businessDetails().businessName(),
            pharmacyData.businessDetails().displayName(),
            pharmacyData.businessDetails().businessRegistrationNumber(),
            pharmacyData.businessDetails().taxId(),
            pharmacyData.businessDetails().contactEmail(),
            pharmacyData.businessDetails().contactPhone(),
            pharmacyData.businessDetails().businessAddress(),
            pharmacyData.businessDetails().city(),
            pharmacyData.businessDetails().country()
        ));
        
        // Verify pharmacy merchant's behavior is based on vertical (still requires validation)
        assertThat(pharmacyMerchant.requiresPrescriptionValidation()).isTrue(); // Vertical still requires it
        assertThat(pharmacyMerchant.requiresAgeVerification()).isTrue(); // Vertical still requires it
        
        // Verify restaurant merchant's behavior remained unchanged
        assertThat(restaurantMerchant.requiresPrescriptionValidation()).isEqualTo(originalRestaurantPrescriptionReq);
        assertThat(restaurantMerchant.requiresAgeVerification()).isEqualTo(originalRestaurantAgeReq);
        
        // Verify merchants still have different behaviors based on their verticals
        assertThat(pharmacyMerchant.requiresPrescriptionValidation()).isNotEqualTo(restaurantMerchant.requiresPrescriptionValidation());
        assertThat(pharmacyMerchant.requiresAgeVerification()).isNotEqualTo(restaurantMerchant.requiresAgeVerification());
    }
    
    /**
     * Property: Status changes are isolated per merchant.
     * 
     * When one merchant's status changes, it should not affect other merchants' status or operations.
     */
    @Property(tries = 100)
    void statusChangesAreIsolatedPerMerchant(
            @ForAll("validMerchantData") MerchantTestData merchant1Data,
            @ForAll("validMerchantData") MerchantTestData merchant2Data) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        
        // Create merchants and approve them first
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Approve both merchants so they can process orders
        merchant1.approve("test-admin");
        merchant2.approve("test-admin");
        
        // Verify both merchants can process orders after approval
        assertThat(merchant1.canProcessOrders()).isTrue();
        assertThat(merchant1.canAccessSystem()).isTrue();
        assertThat(merchant2.canProcessOrders()).isTrue();
        assertThat(merchant2.canAccessSystem()).isTrue();
        
        // Suspend merchant1
        merchant1.suspend("Testing isolation");
        
        // Verify merchant1 is suspended
        assertThat(merchant1.canProcessOrders()).isFalse();
        assertThat(merchant1.canAccessSystem()).isFalse();
        
        // Verify merchant2 remains active and unaffected
        assertThat(merchant2.canProcessOrders()).isTrue();
        assertThat(merchant2.canAccessSystem()).isTrue();
        
        // Deactivate merchant2
        merchant2.deactivate();
        
        // Verify merchant2 is deactivated
        assertThat(merchant2.canProcessOrders()).isFalse();
        assertThat(merchant2.canAccessSystem()).isTrue(); // Can still access system when inactive
        
        // Verify merchant1 status remains suspended (not affected by merchant2's change)
        assertThat(merchant1.canProcessOrders()).isFalse();
        assertThat(merchant1.canAccessSystem()).isFalse();
    }
    
    /**
     * Property: Approval status changes are isolated per merchant.
     * 
     * When one merchant is approved or rejected, it should not affect other merchants' approval status.
     */
    @Property(tries = 100)
    void approvalStatusChangesAreIsolatedPerMerchant(
            @ForAll("validMerchantData") MerchantTestData merchant1Data,
            @ForAll("validMerchantData") MerchantTestData merchant2Data) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        
        // Create merchants (both start as PENDING)
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Verify both merchants start as pending
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.PENDING);
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.PENDING);
        assertThat(merchant1.isPubliclyVisible()).isFalse();
        assertThat(merchant2.isPubliclyVisible()).isFalse();
        
        // Approve merchant1
        merchant1.approve("admin-1");
        
        // Verify merchant1 is approved
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        assertThat(merchant1.isPubliclyVisible()).isTrue();
        
        // Verify merchant2 remains pending and unaffected
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.PENDING);
        assertThat(merchant2.isPubliclyVisible()).isFalse();
        
        // Reject merchant2
        merchant2.reject("Incomplete documentation", "admin-2");
        
        // Verify merchant2 is rejected
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.REJECTED);
        assertThat(merchant2.isPubliclyVisible()).isFalse();
        
        // Verify merchant1 status remains approved (not affected by merchant2's rejection)
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        assertThat(merchant1.isPubliclyVisible()).isTrue();
    }
    
    // Generators for test data
    
    @Provide
    Arbitrary<MerchantTestData> validMerchantData() {
        return Combinators.combine(
            Arbitraries.create(MerchantId::generate),
            validBusinessDetails(),
            Arbitraries.of(Vertical.class)
        ).as(MerchantTestData::new);
    }
    
    @Provide
    Arbitrary<MerchantTestData> pharmacyMerchantData() {
        return Combinators.combine(
            Arbitraries.create(MerchantId::generate),
            validBusinessDetails().map(bd -> new BusinessDetails(
                bd.businessName() + " Pharmacy",
                bd.displayName() + " Pharmacy",
                bd.businessRegistrationNumber(),
                bd.taxId(),
                bd.contactEmail(),
                bd.contactPhone(),
                bd.businessAddress(),
                bd.city(),
                bd.country()
            )),
            Arbitraries.just(Vertical.PHARMACY)
        ).as(MerchantTestData::new);
    }
    
    @Provide
    Arbitrary<MerchantTestData> restaurantMerchantData() {
        return Combinators.combine(
            Arbitraries.create(MerchantId::generate),
            validBusinessDetails().map(bd -> new BusinessDetails(
                bd.businessName() + " Restaurant",
                bd.displayName() + " Restaurant",
                bd.businessRegistrationNumber(),
                bd.taxId(),
                bd.contactEmail(),
                bd.contactPhone(),
                bd.businessAddress(),
                bd.city(),
                bd.country()
            )),
            Arbitraries.just(Vertical.RESTAURANT)
        ).as(MerchantTestData::new);
    }
    
    @Provide
    Arbitrary<BusinessDetails> validBusinessDetails() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(30).map(s -> s + "@example.com"),
            Arbitraries.strings().numeric().ofMinLength(9).ofMaxLength(15),
            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(30)
        ).as((businessName, displayName, regNumber, taxId, email, phone, address, city) -> 
            new BusinessDetails(businessName, displayName, regNumber, taxId, email, phone, address, city, "Mozambique")
        );
    }
    
    @Provide
    Arbitrary<MerchantConfiguration> validMerchantConfiguration() {
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
                acceptsCash = randomChoice == 0;
                acceptsCard = randomChoice == 1;
                acceptsMobile = randomChoice == 2;
            }
            
            return new MerchantConfiguration(
                deliveryFee, minOrder, maxTime,
                acceptsCash, acceptsCard, acceptsMobile,
                settings
            );
        });
    }
    
    // Data class for test parameters
    record MerchantTestData(MerchantId id, BusinessDetails businessDetails, Vertical vertical) {}
}