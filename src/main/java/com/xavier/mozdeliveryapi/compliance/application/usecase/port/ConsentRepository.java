package com.xavier.mozdeliveryapi.compliance.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;

/**
 * Repository interface for consent management.
 */
public interface ConsentRepository {
    
    /**
     * Save a consent.
     */
    Consent save(Consent consent);
    
    /**
     * Find consent by ID.
     */
    Optional<Consent> findById(ConsentId consentId);
    
    /**
     * Find all consents for a data subject.
     */
    List<Consent> findByDataSubjectId(DataSubjectId dataSubjectId);
    
    /**
     * Find all consents for a data subject within a tenant.
     */
    List<Consent> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId);
    
    /**
     * Find consent by data subject, tenant, and consent type.
     */
    Optional<Consent> findByDataSubjectIdAndTenantIdAndConsentType(
        DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType);
    
    /**
     * Find all active consents for a data subject and tenant.
     */
    List<Consent> findActiveConsentsByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId);
    
    /**
     * Find all expired consents that need renewal notification.
     */
    List<Consent> findExpiredConsents();
    
    /**
     * Delete all consents for a data subject (for data deletion requests).
     */
    void deleteByDataSubjectId(DataSubjectId dataSubjectId);
}