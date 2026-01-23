package com.xavier.mozdeliveryapi.compliance.domain.valueobject;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;

/**
 * Types of consent that can be given by data subjects.
 */
public enum ConsentType {
    /**
     * Consent for processing personal data for order fulfillment.
     */
    ORDER_PROCESSING("Processing personal data for order fulfillment"),
    
    /**
     * Consent for marketing communications.
     */
    MARKETING("Marketing communications and promotional offers"),
    
    /**
     * Consent for analytics and performance tracking.
     */
    ANALYTICS("Analytics and performance tracking"),
    
    /**
     * Consent for location tracking during delivery.
     */
    LOCATION_TRACKING("Location tracking for delivery services"),
    
    /**
     * Consent for sharing data with third-party partners.
     */
    THIRD_PARTY_SHARING("Sharing data with third-party partners"),
    
    /**
     * Consent for storing payment information.
     */
    PAYMENT_DATA_STORAGE("Storing payment information for future use");
    
    private final String description;
    
    ConsentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this consent type is required for basic service functionality.
     */
    public boolean isRequired() {
        return this == ORDER_PROCESSING;
    }
}