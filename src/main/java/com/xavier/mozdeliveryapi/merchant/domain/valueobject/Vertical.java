package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

/**
 * Enumeration of supported business verticals.
 */
public enum Vertical {
    RESTAURANT("Restaurant"),
    GROCERY("Grocery Store"),
    PHARMACY("Pharmacy"),
    CONVENIENCE("Convenience Store"),
    ELECTRONICS("Electronics Store"),
    FLORIST("Florist"),
    BEVERAGES("Beverages"),
    FUEL_STATION("Fuel Station");
    
    private final String displayName;
    
    Vertical(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this vertical requires prescription validation.
     */
    public boolean requiresPrescriptionValidation() {
        return this == PHARMACY;
    }
    
    /**
     * Check if this vertical requires age verification.
     */
    public boolean requiresAgeVerification() {
        return this == PHARMACY || this == BEVERAGES;
    }
    
    /**
     * Check if this vertical supports inventory management.
     */
    public boolean supportsInventoryManagement() {
        return this != FUEL_STATION;
    }
}