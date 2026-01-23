package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataDeletionRequest;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataPortabilityRequest;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;

/**
 * Domain service for GDPR compliance operations.
 */
public interface GdprComplianceService {
    
    /**
     * Give consent for a specific purpose.
     */
    Consent giveConsent(DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType, 
                       String purpose, String ipAddress, String userAgent);
    
    /**
     * Withdraw consent.
     */
    void withdrawConsent(ConsentId consentId);
    
    /**
     * Check if data subject has valid consent for a specific type.
     */
    boolean hasValidConsent(DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType);
    
    /**
     * Get all consents for a data subject.
     */
    List<Consent> getConsentsForDataSubject(DataSubjectId dataSubjectId, TenantId tenantId);
    
    /**
     * Request data portability.
     */
    DataPortabilityRequest requestDataPortability(DataSubjectId dataSubjectId, TenantId tenantId, String format);
    
    /**
     * Request data deletion.
     */
    DataDeletionRequest requestDataDeletion(DataSubjectId dataSubjectId, TenantId tenantId, String reason);
    
    /**
     * Process expired consents and send renewal notifications.
     */
    void processExpiredConsents();
    
    /**
     * Validate that required consents are in place for data processing.
     */
    void validateRequiredConsents(DataSubjectId dataSubjectId, TenantId tenantId, List<ConsentType> requiredTypes);
}