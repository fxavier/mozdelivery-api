package com.xavier.mozdeliveryapi.merchant.application.dto;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Request DTO for merchant registration.
 */
public record MerchantRegistrationRequest(
    String businessName,
    String displayName,
    String businessRegistrationNumber,
    String taxId,
    String contactEmail,
    String contactPhone,
    String businessAddress,
    String city,
    String country,
    Vertical vertical
) {
    
    public void validate() {
        if (businessName == null || businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Business name is required");
        }
        
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name is required");
        }
        
        if (contactEmail == null || !contactEmail.contains("@")) {
            throw new IllegalArgumentException("Valid contact email is required");
        }
        
        if (contactPhone == null || contactPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone is required");
        }
        
        if (businessAddress == null || businessAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Business address is required");
        }
        
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        
        if (vertical == null) {
            throw new IllegalArgumentException("Vertical is required");
        }
    }
}