package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;

import java.util.List;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.AgeVerification;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.Prescription;
import com.xavier.mozdeliveryapi.order.domain.valueobject.RestrictedItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;

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