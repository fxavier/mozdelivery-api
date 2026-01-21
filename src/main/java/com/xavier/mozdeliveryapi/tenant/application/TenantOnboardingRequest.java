package com.xavier.mozdeliveryapi.tenant.application;

import com.xavier.mozdeliveryapi.tenant.domain.Vertical;

import java.util.Objects;

/**
 * Request object for tenant onboarding.
 */
public record TenantOnboardingRequest(
    String tenantName,
    Vertical vertical,
    String contactEmail,
    String contactPhone,
    String businessAddress
) {
    
    public TenantOnboardingRequest {
        Objects.requireNonNull(tenantName, "Tenant name cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        Objects.requireNonNull(contactEmail, "Contact email cannot be null");
        Objects.requireNonNull(contactPhone, "Contact phone cannot be null");
        Objects.requireNonNull(businessAddress, "Business address cannot be null");
        
        validateTenantName(tenantName);
        validateEmail(contactEmail);
        validatePhone(contactPhone);
    }
    
    private void validateTenantName(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Tenant name cannot be empty");
        }
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Tenant name cannot exceed 255 characters");
        }
    }
    
    private void validateEmail(String email) {
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Contact email cannot be empty");
        }
        if (!trimmed.contains("@")) {
            throw new IllegalArgumentException("Contact email must be valid");
        }
    }
    
    private void validatePhone(String phone) {
        String trimmed = phone.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Contact phone cannot be empty");
        }
    }
}