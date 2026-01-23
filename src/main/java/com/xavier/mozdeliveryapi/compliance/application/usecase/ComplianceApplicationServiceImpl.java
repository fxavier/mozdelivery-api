package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataDeletionRequest;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataPortabilityRequest;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;
import com.xavier.mozdeliveryapi.compliance.application.usecase.GdprComplianceService;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.application.dto.ConsentResponse;
import com.xavier.mozdeliveryapi.compliance.application.dto.DataDeletionRequestRequest;
import com.xavier.mozdeliveryapi.compliance.application.dto.DataPortabilityRequestRequest;
import com.xavier.mozdeliveryapi.compliance.application.dto.GiveConsentRequest;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Implementation of compliance application service.
 */
@Service
@Transactional
public class ComplianceApplicationServiceImpl implements ComplianceApplicationService {
    
    private final GdprComplianceService gdprComplianceService;
    
    public ComplianceApplicationServiceImpl(GdprComplianceService gdprComplianceService) {
        this.gdprComplianceService = Objects.requireNonNull(gdprComplianceService);
    }
    
    @Override
    public ConsentResponse giveConsent(GiveConsentRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        DataSubjectId dataSubjectId = DataSubjectId.of(request.dataSubjectId());
        TenantId tenantId = TenantId.of(request.tenantId());
        
        Consent consent = gdprComplianceService.giveConsent(
            dataSubjectId, tenantId, request.consentType(), 
            request.purpose(), request.ipAddress(), request.userAgent());
        
        return ConsentResponse.from(consent);
    }
    
    @Override
    public void withdrawConsent(String consentId) {
        Objects.requireNonNull(consentId, "Consent ID cannot be null");
        
        ConsentId id = ConsentId.of(consentId);
        gdprComplianceService.withdrawConsent(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsForDataSubject(String dataSubjectId, String tenantId) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        DataSubjectId subjectId = DataSubjectId.of(dataSubjectId);
        TenantId tenant = TenantId.of(tenantId);
        
        List<Consent> consents = gdprComplianceService.getConsentsForDataSubject(subjectId, tenant);
        return consents.stream()
                      .map(ConsentResponse::from)
                      .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasValidConsent(String dataSubjectId, String tenantId, String consentType) {
        Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(consentType, "Consent type cannot be null");
        
        DataSubjectId subjectId = DataSubjectId.of(dataSubjectId);
        TenantId tenant = TenantId.of(tenantId);
        ConsentType type = ConsentType.valueOf(consentType.toUpperCase());
        
        return gdprComplianceService.hasValidConsent(subjectId, tenant, type);
    }
    
    @Override
    public String requestDataPortability(DataPortabilityRequestRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        DataSubjectId dataSubjectId = DataSubjectId.of(request.dataSubjectId());
        TenantId tenantId = TenantId.of(request.tenantId());
        
        DataPortabilityRequest portabilityRequest = gdprComplianceService.requestDataPortability(
            dataSubjectId, tenantId, request.format());
        
        return portabilityRequest.getRequestId().toString();
    }
    
    @Override
    public String requestDataDeletion(DataDeletionRequestRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        DataSubjectId dataSubjectId = DataSubjectId.of(request.dataSubjectId());
        TenantId tenantId = TenantId.of(request.tenantId());
        
        DataDeletionRequest deletionRequest = gdprComplianceService.requestDataDeletion(
            dataSubjectId, tenantId, request.reason());
        
        return deletionRequest.getRequestId().toString();
    }
}