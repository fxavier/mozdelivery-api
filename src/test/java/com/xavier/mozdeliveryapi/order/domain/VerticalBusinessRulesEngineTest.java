package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.tenant.domain.Vertical;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VerticalBusinessRulesEngineTest {
    
    private VerticalBusinessRulesEngine rulesEngine;
    
    @BeforeEach
    void setUp() {
        rulesEngine = new VerticalBusinessRulesEngineImpl();
    }
    
    @Test
    void shouldValidateNormalItemsInPharmacy() {
        // Given
        OrderItem normalItem = OrderItem.of("item-1", "Vitamins", 1, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(normalItem);
        
        // When
        ValidationResult result = rulesEngine.validateOrderItems(items, Vertical.PHARMACY);
        
        // Then
        assertThat(result.isValid()).isTrue();
    }
    
    @Test
    void shouldDetectPrescriptionRequiredItems() {
        // Given
        OrderItem prescriptionItem = OrderItem.of("item-1", "Prescription Medicine", 1, 
            Money.of(BigDecimal.valueOf(50.00), Currency.USD));
        List<OrderItem> items = List.of(prescriptionItem);
        
        // When
        ValidationResult result = rulesEngine.validateOrderItems(items, Vertical.PHARMACY);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("PRESCRIPTION_REQUIRED");
    }
    
    @Test
    void shouldDetectAgeRestrictedItems() {
        // Given
        OrderItem ageRestrictedItem = OrderItem.of("item-1", "Medicine for Adults", 1, 
            Money.of(BigDecimal.valueOf(25.00), Currency.USD));
        List<OrderItem> items = List.of(ageRestrictedItem);
        
        // When
        ValidationResult result = rulesEngine.validateOrderItems(items, Vertical.PHARMACY);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("AGE_VERIFICATION_REQUIRED");
    }
    
    @Test
    void shouldDetectAlcoholInBeverages() {
        // Given
        OrderItem alcoholItem = OrderItem.of("item-1", "Beer Alcohol", 1, 
            Money.of(BigDecimal.valueOf(15.00), Currency.USD));
        List<OrderItem> items = List.of(alcoholItem);
        
        // When
        ValidationResult result = rulesEngine.validateOrderItems(items, Vertical.BEVERAGES);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("AGE_VERIFICATION_REQUIRED");
    }
    
    @Test
    void shouldValidatePrescriptionRequirements() {
        // Given
        OrderItem prescriptionItem = OrderItem.of("item-1", "Prescription Medicine", 1, 
            Money.of(BigDecimal.valueOf(50.00), Currency.USD));
        Order order = createOrderWithItems(List.of(prescriptionItem));
        
        Prescription validPrescription = new Prescription(
            "RX-123",
            "Dr. Smith",
            "DR123456",
            "John Doe",
            "ID123",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(30),
            "Prescription Medicine",
            "10mg",
            1,
            "Take once daily"
        );
        
        // When
        ValidationResult result = rulesEngine.validatePrescriptionRequirements(order, List.of(validPrescription));
        
        // Then
        assertThat(result.isValid()).isTrue();
    }
    
    @Test
    void shouldFailValidationWithoutPrescription() {
        // Given
        OrderItem prescriptionItem = OrderItem.of("item-1", "Prescription Medicine", 1, 
            Money.of(BigDecimal.valueOf(50.00), Currency.USD));
        Order order = createOrderWithItems(List.of(prescriptionItem));
        
        // When
        ValidationResult result = rulesEngine.validatePrescriptionRequirements(order, List.of());
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("MISSING_PRESCRIPTION");
    }
    
    @Test
    void shouldValidateAgeVerification() {
        // Given
        OrderItem ageRestrictedItem = OrderItem.of("item-1", "Medicine for Adults", 1, 
            Money.of(BigDecimal.valueOf(25.00), Currency.USD));
        Order order = createOrderWithItems(List.of(ageRestrictedItem));
        
        AgeVerification validAge = AgeVerification.verified(
            "ID_CARD", "ID123456", LocalDate.now().minusYears(25), "John Doe");
        
        // When
        ValidationResult result = rulesEngine.validateAgeVerification(order, validAge);
        
        // Then
        assertThat(result.isValid()).isTrue();
    }
    
    @Test
    void shouldFailAgeVerificationForUnderage() {
        // Given
        OrderItem ageRestrictedItem = OrderItem.of("item-1", "Medicine for Adults", 1, 
            Money.of(BigDecimal.valueOf(25.00), Currency.USD));
        Order order = createOrderWithItems(List.of(ageRestrictedItem));
        
        AgeVerification underageVerification = AgeVerification.verified(
            "ID_CARD", "ID123456", LocalDate.now().minusYears(15), "John Doe");
        
        // When
        ValidationResult result = rulesEngine.validateAgeVerification(order, underageVerification);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("AGE_REQUIREMENT_NOT_MET");
    }
    
    @Test
    void shouldIdentifyRestrictedItems() {
        // Given
        OrderItem normalItem = OrderItem.of("item-1", "Vitamins", 1, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        OrderItem prescriptionItem = OrderItem.of("item-2", "Prescription Medicine", 1, 
            Money.of(BigDecimal.valueOf(50.00), Currency.USD));
        List<OrderItem> items = List.of(normalItem, prescriptionItem);
        
        // When
        List<RestrictedItem> restrictedItems = rulesEngine.getRestrictedItems(items, Vertical.PHARMACY);
        
        // Then
        assertThat(restrictedItems).hasSize(1);
        assertThat(restrictedItems.get(0).productName()).isEqualTo("Prescription Medicine");
        assertThat(restrictedItems.get(0).requiresPrescriptionValidation()).isTrue();
    }
    
    @Test
    void shouldDetectPrescriptionRequirement() {
        // Given
        OrderItem prescriptionItem = OrderItem.of("item-1", "Prescription Medicine", 1, 
            Money.of(BigDecimal.valueOf(50.00), Currency.USD));
        Order order = createOrderWithItems(List.of(prescriptionItem));
        
        // When
        boolean requiresPrescription = rulesEngine.requiresPrescriptionValidation(order, Vertical.PHARMACY);
        
        // Then
        assertThat(requiresPrescription).isTrue();
    }
    
    @Test
    void shouldDetectAgeVerificationRequirement() {
        // Given
        OrderItem ageRestrictedItem = OrderItem.of("item-1", "Medicine for Adults", 1, 
            Money.of(BigDecimal.valueOf(25.00), Currency.USD));
        Order order = createOrderWithItems(List.of(ageRestrictedItem));
        
        // When
        boolean requiresAgeVerification = rulesEngine.requiresAgeVerification(order, Vertical.PHARMACY);
        
        // Then
        assertThat(requiresAgeVerification).isTrue();
    }
    
    private Order createOrderWithItems(List<OrderItem> items) {
        return TestOrderFactory.createOrderWithItems(items);
    }
}