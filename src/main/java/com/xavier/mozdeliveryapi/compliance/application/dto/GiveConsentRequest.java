package com.xavier.mozdeliveryapi.compliance.application.dto;

import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Request to give consent for data processing.
 */
public record GiveConsentRequest(
    String dataSubjectId,
    String tenantId,
    ConsentType consentType,
    String purpose,
    String ipAddress,
    String userAgent
) {
    public GiveConsentRequest {
        if (dataSubjectId == null || dataSubjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Data subject ID cannot be null or empty");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or empty");
        }
        if (consentType == null) {
            throw new IllegalArgumentException("Consent type cannot be null");
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose cannot be null or empty");
        }
    }
}