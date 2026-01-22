package com.xavier.mozdeliveryapi.compliance.application;

import java.util.List;

/**
 * Application service for compliance operations.
 */
public interface ComplianceApplicationService {
    
    /**
     * Give consent for data processing.
     */
    ConsentResponse giveConsent(GiveConsentRequest request);
    
    /**
     * Withdraw consent.
     */
    void withdrawConsent(String consentId);
    
    /**
     * Get all consents for a data subject.
     */
    List<ConsentResponse> getConsentsForDataSubject(String dataSubjectId, String tenantId);
    
    /**
     * Check if data subject has valid consent for a specific type.
     */
    boolean hasValidConsent(String dataSubjectId, String tenantId, String consentType);
    
    /**
     * Request data portability.
     */
    String requestDataPortability(DataPortabilityRequestRequest request);
    
    /**
     * Request data deletion.
     */
    String requestDataDeletion(DataDeletionRequestRequest request);
}