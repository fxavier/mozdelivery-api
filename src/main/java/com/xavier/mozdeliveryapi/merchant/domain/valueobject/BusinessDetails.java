package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing business details for merchant registration.
 */
public record BusinessDetails(
    String businessName,
    String displayName,
    String businessRegistrationNumber,
    String taxId,
    String contactEmail,
    String contactPhone,
    String businessAddress,
    String city,
    String country
) implements ValueObject {
    
    public BusinessDetails {
        Objects.requireNonNull(businessName, "Business name cannot be null");
        Objects.requireNonNull(displayName, "Display name cannot be null");
        Objects.requireNonNull(contactEmail, "Contact email cannot be null");
        Objects.requireNonNull(contactPhone, "Contact phone cannot be null");
        Objects.requireNonNull(businessAddress, "Business address cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(country, "Country cannot be null");
        
        if (businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Business name cannot be empty");
        }
        
        if (displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        
        if (!contactEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}