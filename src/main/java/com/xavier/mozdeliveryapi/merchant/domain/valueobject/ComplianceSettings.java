package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

import java.util.Map;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing compliance settings for a merchant.
 */
public record ComplianceSettings(
    boolean gdprEnabled,
    boolean auditLoggingEnabled,
    boolean dataEncryptionRequired,
    Map<String, Object> verticalSpecificSettings
) implements ValueObject {
    
    public ComplianceSettings {
        Objects.requireNonNull(verticalSpecificSettings, "Vertical specific settings cannot be null");
    }
    
    /**
     * Create default compliance settings for a vertical.
     */
    public static ComplianceSettings defaultFor(Vertical vertical) {
        Map<String, Object> verticalSettings = switch (vertical) {
            case PHARMACY -> Map.of(
                "prescriptionValidationRequired", true,
                "ageVerificationRequired", true,
                "controlledSubstanceTracking", true,
                "pharmacistApprovalRequired", true
            );
            case BEVERAGES -> Map.of(
                "ageVerificationRequired", true,
                "alcoholLicenseRequired", true
            );
            default -> Map.of();
        };
        
        return new ComplianceSettings(
            true, // GDPR always enabled
            true, // Audit logging always enabled
            vertical == Vertical.PHARMACY, // Data encryption required for pharmacy
            verticalSettings
        );
    }
    
    /**
     * Check if prescription validation is required.
     */
    public boolean requiresPrescriptionValidation() {
        return Boolean.TRUE.equals(verticalSpecificSettings.get("prescriptionValidationRequired"));
    }
    
    /**
     * Check if age verification is required.
     */
    public boolean requiresAgeVerification() {
        return Boolean.TRUE.equals(verticalSpecificSettings.get("ageVerificationRequired"));
    }
}