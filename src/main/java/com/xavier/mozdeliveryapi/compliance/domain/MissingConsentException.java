package com.xavier.mozdeliveryapi.compliance.domain;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Exception thrown when required consent is missing.
 */
public class MissingConsentException extends RuntimeException {
    
    private final DataSubjectId dataSubjectId;
    private final TenantId tenantId;
    private final ConsentType consentType;
    
    public MissingConsentException(DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType) {
        super(String.format("Missing required consent: %s for data subject %s in tenant %s", 
                           consentType, dataSubjectId, tenantId));
        this.dataSubjectId = dataSubjectId;
        this.tenantId = tenantId;
        this.consentType = consentType;
    }
    
    public DataSubjectId getDataSubjectId() {
        return dataSubjectId;
    }
    
    public TenantId getTenantId() {
        return tenantId;
    }
    
    public ConsentType getConsentType() {
        return consentType;
    }
}