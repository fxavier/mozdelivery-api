package com.xavier.mozdeliveryapi.compliance.application.usecase;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataDeletionRequest;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataPortabilityRequest;
import com.xavier.mozdeliveryapi.compliance.domain.exception.ConsentNotFoundException;
import com.xavier.mozdeliveryapi.compliance.domain.exception.MissingConsentException;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentStatus;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataDeletionRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataPortabilityRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.ConsentRepository;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.DataDeletionRequestRepository;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.DataPortabilityRequestRepository;

/**
 * Implementation of GDPR compliance service.
 */
@Service
public class GdprComplianceServiceImpl implements GdprComplianceService {
    
    private final ConsentRepository consentRepository;
    private final DataPortabilityRequestRepository dataPortabilityRequestRepository;
    private final DataDeletionRequestRepository dataDeletionRequestRepository;
    
    public GdprComplianceServiceImpl(ConsentRepository consentRepository,
                                     DataPortabilityRequestRepository dataPortabilityRequestRepository,
                                     DataDeletionRequestRepository dataDeletionRequestRepository) {
        this.consentRepository = Objects.requireNonNull(consentRepository);
        this.dataPortabilityRequestRepository = Objects.requireNonNull(dataPortabilityRequestRepository);
        this.dataDeletionRequestRepository = Objects.requireNonNull(dataDeletionRequestRepository);
    }
    
    @Override
    public Consent giveConsent(DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType,
                              String purpose, String ipAddress, String userAgent) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(consentType, "Consent type cannot be null");
        
        // Check if consent already exists
        var existingConsent = consentRepository.findByDataSubjectIdAndTenantIdAndConsentType(
            dataSubjectId, tenantId, consentType);
        
        if (existingConsent.isPresent()) {
            Consent consent = existingConsent.get();
            if (consent.isValid()) {
                return consent; // Already has valid consent
            } else if (consent.getStatus() == ConsentStatus.EXPIRED) {
                // Renew expired consent
                consent.renew(ipAddress, userAgent);
                return consentRepository.save(consent);
            } else {
                // Withdraw old consent and create new one
                consent.withdraw();
                consentRepository.save(consent);
            }
        }
        
        // Create new consent
        Consent newConsent = new Consent(ConsentId.generate(), dataSubjectId, tenantId, 
                                        consentType, purpose, ipAddress, userAgent);
        return consentRepository.save(newConsent);
    }
    
    @Override
    public void withdrawConsent(ConsentId consentId) {
        Objects.requireNonNull(consentId, "Consent ID cannot be null");
        
        Consent consent = consentRepository.findById(consentId)
            .orElseThrow(() -> new ConsentNotFoundException(consentId));
        
        consent.withdraw();
        consentRepository.save(consent);
    }
    
    @Override
    public boolean hasValidConsent(DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(consentType, "Consent type cannot be null");
        
        return consentRepository.findByDataSubjectIdAndTenantIdAndConsentType(
            dataSubjectId, tenantId, consentType)
            .map(Consent::isValid)
            .orElse(false);
    }
    
    @Override
    public List<Consent> getConsentsForDataSubject(DataSubjectId dataSubjectId, TenantId tenantId) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return consentRepository.findByDataSubjectIdAndTenantId(dataSubjectId, tenantId);
    }
    
    @Override
    public DataPortabilityRequest requestDataPortability(DataSubjectId dataSubjectId, TenantId tenantId, String format) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(format, "Format cannot be null");
        
        DataPortabilityRequest request = new DataPortabilityRequest(
            DataPortabilityRequestId.generate(), dataSubjectId, tenantId, format);
        
        return dataPortabilityRequestRepository.save(request);
    }
    
    @Override
    public DataDeletionRequest requestDataDeletion(DataSubjectId dataSubjectId, TenantId tenantId, String reason) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        DataDeletionRequest request = new DataDeletionRequest(
            DataDeletionRequestId.generate(), dataSubjectId, tenantId, reason);
        
        return dataDeletionRequestRepository.save(request);
    }
    
    @Override
    public void processExpiredConsents() {
        List<Consent> expiredConsents = consentRepository.findExpiredConsents();
        
        for (Consent consent : expiredConsents) {
            // Check if consent is actually expired (triggers status update)
            consent.isValid();
            consentRepository.save(consent);
        }
    }
    
    @Override
    public void validateRequiredConsents(DataSubjectId dataSubjectId, TenantId tenantId, List<ConsentType> requiredTypes) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(requiredTypes, "Required types cannot be null");
        
        for (ConsentType requiredType : requiredTypes) {
            if (!hasValidConsent(dataSubjectId, tenantId, requiredType)) {
                throw new MissingConsentException(dataSubjectId, tenantId, requiredType);
            }
        }
    }
}
