package com.xavier.mozdeliveryapi.compliance.infra.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.ConsentRepository;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentStatus;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * In-memory implementation of consent repository for development/testing.
 * In production, this would be replaced with a JPA implementation.
 */
@Repository
public class ConsentRepositoryImpl implements ConsentRepository {
    
    private final Map<ConsentId, Consent> consents = new ConcurrentHashMap<>();
    
    @Override
    public Consent save(Consent consent) {
        consents.put(consent.getConsentId(), consent);
        return consent;
    }
    
    @Override
    public Optional<Consent> findById(ConsentId consentId) {
        return Optional.ofNullable(consents.get(consentId));
    }
    
    @Override
    public List<Consent> findByDataSubjectId(DataSubjectId dataSubjectId) {
        return consents.values().stream()
                      .filter(consent -> consent.getDataSubjectId().equals(dataSubjectId))
                      .toList();
    }
    
    @Override
    public List<Consent> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId) {
        return consents.values().stream()
                      .filter(consent -> consent.getDataSubjectId().equals(dataSubjectId) &&
                                       consent.getTenantId().equals(tenantId))
                      .toList();
    }
    
    @Override
    public Optional<Consent> findByDataSubjectIdAndTenantIdAndConsentType(
            DataSubjectId dataSubjectId, TenantId tenantId, ConsentType consentType) {
        return consents.values().stream()
                      .filter(consent -> consent.getDataSubjectId().equals(dataSubjectId) &&
                                       consent.getTenantId().equals(tenantId) &&
                                       consent.getConsentType().equals(consentType))
                      .findFirst();
    }
    
    @Override
    public List<Consent> findActiveConsentsByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId) {
        return consents.values().stream()
                      .filter(consent -> consent.getDataSubjectId().equals(dataSubjectId) &&
                                       consent.getTenantId().equals(tenantId) &&
                                       consent.getStatus() == ConsentStatus.GIVEN)
                      .toList();
    }
    
    @Override
    public List<Consent> findExpiredConsents() {
        Instant now = Instant.now();
        return consents.values().stream()
                      .filter(consent -> consent.getStatus() == ConsentStatus.GIVEN &&
                                       consent.getExpiresAt() != null &&
                                       now.isAfter(consent.getExpiresAt()))
                      .toList();
    }
    
    @Override
    public void deleteByDataSubjectId(DataSubjectId dataSubjectId) {
        consents.entrySet().removeIf(entry -> 
            entry.getValue().getDataSubjectId().equals(dataSubjectId));
    }
}