package com.xavier.mozdeliveryapi.order.domain.valueobject;


import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import java.util.Set;

/**
 * Value object representing a restricted item that requires special handling.
 */
public record RestrictedItem(
    String productId,
    String productName,
    RestrictionType restrictionType,
    int minimumAge,
    boolean requiresPrescription,
    Set<String> requiredDocuments,
    String restrictionReason
) implements ValueObject {
    
    public RestrictedItem {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(restrictionType, "Restriction type cannot be null");
        Objects.requireNonNull(requiredDocuments, "Required documents cannot be null");
        
        if (productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        if (minimumAge < 0) {
            throw new IllegalArgumentException("Minimum age cannot be negative");
        }
        
        if (requiresPrescription && restrictionType != RestrictionType.PRESCRIPTION_REQUIRED) {
            throw new IllegalArgumentException("Prescription required items must have PRESCRIPTION_REQUIRED restriction type");
        }
    }
    
    /**
     * Check if age verification is required for this item.
     */
    public boolean requiresAgeVerification() {
        return minimumAge > 0 || restrictionType == RestrictionType.AGE_RESTRICTED;
    }
    
    /**
     * Check if the given age meets the minimum requirement.
     */
    public boolean isAgeEligible(int age) {
        return age >= minimumAge;
    }
    
    /**
     * Check if prescription is required for this item.
     */
    public boolean requiresPrescriptionValidation() {
        return requiresPrescription || restrictionType == RestrictionType.PRESCRIPTION_REQUIRED;
    }
}