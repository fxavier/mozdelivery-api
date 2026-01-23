package com.xavier.mozdeliveryapi.merchant.domain;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.BusinessDetails;
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

/**
 * Property-based test for merchant data isolation at the domain level.
 * 
 * **Property 1: Merchant Data Isolation**
 * **Validates: Requirements 1.1, 1.3**
 * 
 * This test verifies that merchant entities maintain data isolation at the domain level.
 */
class MerchantDataIsolationSimplePropertyTest {
    
    /**
     * Property: Merchant configuration independence.
     * 
     * Configuration changes to one merchant should not affect the configuration
     * of any other merchant at the domain level.
     */
    @Property(tries = 100)
    void merchantConfigurationIndependence(
            @ForAll("validMerchantData") MerchantData merchant1Data,
            @ForAll("validMerchantData") MerchantData merchant2Data,
            @ForAll("validMerchantConfiguration") MerchantConfiguration config1,
            @ForAll("validMerchantConfiguration") MerchantConfiguration config2) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        Assume.that(!config1.equals(config2));
        
        // Create merchants
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Approve merchants so they can update configurations
        merchant1.approve("test-admin");
        merchant2.approve("test-admin");
        
        // Update configurations independently
        merchant1.updateConfiguration(config1);
        merchant2.updateConfiguration(config2);
        
        // Verify configurations are independent
        assertThat(merchant1.getConfiguration()).isEqualTo(config1);
        assertThat(merchant2.getConfiguration()).isEqualTo(config2);
        
        // Configurations should be different
        assertThat(merchant1.getConfiguration()).isNotEqualTo(merchant2.getConfiguration());
        
        // Verify merchant identities are preserved
        assertThat(merchant1.getMerchantId()).isEqualTo(merchant1Data.id());
        assertThat(merchant2.getMerchantId()).isEqualTo(merchant2Data.id());
        assertThat(merchant1.getBusinessName()).isEqualTo(merchant1Data.businessDetails().businessName());
        assertThat(merchant2.getBusinessName()).isEqualTo(merchant2Data.businessDetails().businessName());
    }
    
    /**
     * Property: Merchant identity isolation.
     * 
     * Each merchant should maintain its unique identity and properties
     * regardless of operations on other merchants.
     */
    @Property(tries = 100)
    void merchantIdentityIsolation(
            @ForAll("validMerchantData") MerchantData merchant1Data,
            @ForAll("validMerchantData") MerchantData merchant2Data) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        Assume.that(!merchant1Data.businessDetails().businessName().equals(merchant2Data.businessDetails().businessName()));
        
        // Create merchants
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Approve merchants first
        merchant1.approve("test-admin");
        merchant2.approve("test-admin");
        
        // Perform operations on merchant1
        merchant1.suspend("Test suspension");
        
        // Verify merchant2 is unaffected
        assertThat(merchant2.canProcessOrders()).isTrue();
        assertThat(merchant2.canAccessSystem()).isTrue();
        
        // Verify merchant1 is affected as expected
        assertThat(merchant1.canProcessOrders()).isFalse();
        assertThat(merchant1.canAccessSystem()).isFalse();
        
        // Verify identities remain distinct
        assertThat(merchant1.getMerchantId()).isNotEqualTo(merchant2.getMerchantId());
        assertThat(merchant1.getBusinessName()).isNotEqualTo(merchant2.getBusinessName());
    }
    
    /**
     * Property: Merchant vertical-specific behavior isolation.
     * 
     * Vertical-specific behaviors should be isolated per merchant
     * and not affect other merchants.
     */
    @Property(tries = 100)
    void merchantVerticalBehaviorIsolation(
            @ForAll("pharmacyMerchantData") MerchantData pharmacyData,
            @ForAll("restaurantMerchantData") MerchantData restaurantData) {
        
        // Create merchants with different verticals
        Merchant pharmacyMerchant = new Merchant(pharmacyData.id(), pharmacyData.businessDetails(), pharmacyData.vertical());
        Merchant restaurantMerchant = new Merchant(restaurantData.id(), restaurantData.businessDetails(), restaurantData.vertical());
        
        // Verify vertical-specific behaviors are isolated
        assertThat(pharmacyMerchant.requiresPrescriptionValidation()).isTrue();
        assertThat(pharmacyMerchant.requiresAgeVerification()).isTrue();
        
        assertThat(restaurantMerchant.requiresPrescriptionValidation()).isFalse();
        assertThat(restaurantMerchant.requiresAgeVerification()).isFalse();
        
        // Approve and then deactivate pharmacy merchant
        pharmacyMerchant.approve("test-admin");
        restaurantMerchant.approve("test-admin");
        
        pharmacyMerchant.deactivate();
        
        // Verify operations on one don't affect the other
        assertThat(pharmacyMerchant.canProcessOrders()).isFalse();
        assertThat(restaurantMerchant.canProcessOrders()).isTrue();
    }
    
    /**
     * Property: Merchant approval status isolation.
     * 
     * Approval status changes should be isolated per merchant.
     */
    @Property(tries = 100)
    void merchantApprovalStatusIsolation(
            @ForAll("validMerchantData") MerchantData merchant1Data,
            @ForAll("validMerchantData") MerchantData merchant2Data) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Data.id().equals(merchant2Data.id()));
        
        // Create merchants (both start as PENDING)
        Merchant merchant1 = new Merchant(merchant1Data.id(), merchant1Data.businessDetails(), merchant1Data.vertical());
        Merchant merchant2 = new Merchant(merchant2Data.id(), merchant2Data.businessDetails(), merchant2Data.vertical());
        
        // Verify both start as pending
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.PENDING);
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.PENDING);
        
        // Approve merchant1
        merchant1.approve("admin-1");
        
        // Verify merchant1 is approved but merchant2 remains pending
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        assertThat(merchant1.isPubliclyVisible()).isTrue();
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.PENDING);
        assertThat(merchant2.isPubliclyVisible()).isFalse();
        
        // Reject merchant2
        merchant2.reject("Incomplete documentation", "admin-2");
        
        // Verify merchant2 is rejected but merchant1 remains approved
        assertThat(merchant2.getStatus()).isEqualTo(MerchantStatus.REJECTED);
        assertThat(merchant2.isPubliclyVisible()).isFalse();
        assertThat(merchant1.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        assertThat(merchant1.isPubliclyVisible()).isTrue();
    }
    
    // Generators for test data
    
    @Provide
    Arbitrary<MerchantData> validMerchantData() {
        return Combinators.combine(
            Arbitraries.create(MerchantId::generate),
            validBusinessDetails(),
            Arbitraries.of(Vertical.class)
        ).as(MerchantData::new);
    }
    
    @Provide
    Arbitrary<MerchantData> pharmacyMerchantData() {
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
        ).as(MerchantData::new);
    }
    
    @Provide
    Arbitrary<MerchantData> restaurantMerchantData() {
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
        ).as(MerchantData::new);
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
    
    // Data classes for test parameters
    
    record MerchantData(MerchantId id, BusinessDetails businessDetails, Vertical vertical) {}
}