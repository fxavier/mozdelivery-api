package com.xavier.mozdeliveryapi.compliance.application.dto;

import java.time.Instant;

import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentStatus;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;

/**
 * Response containing consent information.
 */
public record ConsentResponse(
    String consentId,
    String dataSubjectId,
    String tenantId,
    ConsentType consentType,
    ConsentStatus status,
    String purpose,
    Instant givenAt,
    Instant withdrawnAt,
    Instant expiresAt,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static ConsentResponse from(Consent consent) {
        return new ConsentResponse(
            consent.getConsentId().toString(),
            consent.getDataSubjectId().toString(),
            consent.getTenantId().toString(),
            consent.getConsentType(),
            consent.getStatus(),
            consent.getPurpose(),
            consent.getGivenAt(),
            consent.getWithdrawnAt(),
            consent.getExpiresAt(),
            consent.getCreatedAt(),
            consent.getUpdatedAt()
        );
    }
}