package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.tenant.domain.Vertical;

import java.util.List;

/**
 * Engine for applying vertical-specific business rules to orders.
 */
public interface VerticalBusinessRulesEngine {
    
    /**
     * Validate an order against vertical-specific rules.
     */
    ValidationResult validateOrder(Order order, Vertical vertical);
    
    /**
     * Validate order items for restrictions.
     */
    ValidationResult validateOrderItems(List<OrderItem> items, Vertical vertical);
    
    /**
     * Validate prescription requirements for pharmacy orders.
     */
    ValidationResult validatePrescriptionRequirements(Order order, List<Prescription> prescriptions);
    
    /**
     * Validate age verification requirements.
     */
    ValidationResult validateAgeVerification(Order order, AgeVerification ageVerification);
    
    /**
     * Get restricted items from an order.
     */
    List<RestrictedItem> getRestrictedItems(List<OrderItem> items, Vertical vertical);
    
    /**
     * Check if an order requires prescription validation.
     */
    boolean requiresPrescriptionValidation(Order order, Vertical vertical);
    
    /**
     * Check if an order requires age verification.
     */
    boolean requiresAgeVerification(Order order, Vertical vertical);
    
    /**
     * Apply vertical-specific processing rules.
     */
    void applyVerticalProcessingRules(Order order, Vertical vertical);
}