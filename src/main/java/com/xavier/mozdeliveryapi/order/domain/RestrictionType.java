package com.xavier.mozdeliveryapi.order.domain;

/**
 * Enumeration of item restriction types.
 */
public enum RestrictionType {
    NONE("No restrictions"),
    AGE_RESTRICTED("Age verification required"),
    PRESCRIPTION_REQUIRED("Valid prescription required"),
    CONTROLLED_SUBSTANCE("Controlled substance - special handling required"),
    ALCOHOL("Alcoholic beverage - age verification required"),
    TOBACCO("Tobacco product - age verification required"),
    ADULT_CONTENT("Adult content - age verification required");
    
    private final String description;
    
    RestrictionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this restriction type requires age verification.
     */
    public boolean requiresAgeVerification() {
        return switch (this) {
            case AGE_RESTRICTED, ALCOHOL, TOBACCO, ADULT_CONTENT -> true;
            case NONE, PRESCRIPTION_REQUIRED, CONTROLLED_SUBSTANCE -> false;
        };
    }
    
    /**
     * Check if this restriction type requires prescription validation.
     */
    public boolean requiresPrescription() {
        return this == PRESCRIPTION_REQUIRED || this == CONTROLLED_SUBSTANCE;
    }
    
    /**
     * Get the minimum age requirement for this restriction type.
     */
    public int getMinimumAge() {
        return switch (this) {
            case ALCOHOL, TOBACCO, ADULT_CONTENT -> 18;
            case AGE_RESTRICTED -> 16;
            case NONE, PRESCRIPTION_REQUIRED, CONTROLLED_SUBSTANCE -> 0;
        };
    }
}