package com.xavier.mozdeliveryapi.compliance.domain;

import com.xavier.mozdeliveryapi.shared.domain.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Consent aggregate representing a data subject's consent for specific data processing activities.
 */
public class Consent extends AggregateRoot<ConsentId> {
    
    private final ConsentId id;
    private final DataSubjectId dataSubjectId;
    private final TenantId tenantId;
    private final ConsentType consentType;
    private ConsentStatus status;
    private final String purpose;
    private final Instant givenAt;
    private Instant withdrawnAt;
    private Instant expiresAt;
    private final String ipAddress;
    private final String userAgent;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for giving new consent
    public Consent(ConsentId id, DataSubjectId dataSubjectId, TenantId tenantId, 
                   ConsentType consentType, String purpose, String ipAddress, String userAgent) {
        this.id = Objects.requireNonNull(id, "Consent ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.consentType = Objects.requireNonNull(consentType, "Consent type cannot be null");
        this.purpose = validatePurpose(purpose);
        this.status = ConsentStatus.GIVEN;
        this.givenAt = Instant.now();
        this.expiresAt = calculateExpirationDate();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new ConsentGivenEvent(id, dataSubjectId, tenantId, consentType, givenAt));
    }
    
    // Constructor for reconstituting from persistence
    public Consent(ConsentId id, DataSubjectId dataSubjectId, TenantId tenantId, 
                   ConsentType consentType, ConsentStatus status, String purpose,
                   Instant givenAt, Instant withdrawnAt, Instant expiresAt,
                   String ipAddress, String userAgent, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Consent ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.consentType = Objects.requireNonNull(consentType, "Consent type cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.purpose = validatePurpose(purpose);
        this.givenAt = Objects.requireNonNull(givenAt, "Given at cannot be null");
        this.withdrawnAt = withdrawnAt;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected ConsentId getId() {
        return id;
    }
    
    /**
     * Withdraw consent.
     */
    public void withdraw() {
        if (status == ConsentStatus.WITHDRAWN) {
            return; // Already withdrawn
        }
        
        this.status = ConsentStatus.WITHDRAWN;
        this.withdrawnAt = Instant.now();
        this.updatedAt = Instant.now();
        
        registerEvent(new ConsentWithdrawnEvent(id, dataSubjectId, tenantId, consentType, withdrawnAt));
    }
    
    /**
     * Check if consent is currently valid.
     */
    public boolean isValid() {
        if (status != ConsentStatus.GIVEN) {
            return false;
        }
        
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
            // Mark as expired
            this.status = ConsentStatus.EXPIRED;
            this.updatedAt = Instant.now();
            registerEvent(new ConsentExpiredEvent(id, dataSubjectId, tenantId, consentType, Instant.now()));
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if consent allows data processing.
     */
    public boolean allowsProcessing() {
        return isValid() && status.allowsProcessing();
    }
    
    /**
     * Renew expired consent.
     */
    public void renew(String ipAddress, String userAgent) {
        if (status != ConsentStatus.EXPIRED) {
            throw new IllegalStateException("Can only renew expired consent");
        }
        
        this.status = ConsentStatus.GIVEN;
        this.expiresAt = calculateExpirationDate();
        this.updatedAt = Instant.now();
        
        registerEvent(new ConsentRenewedEvent(id, dataSubjectId, tenantId, consentType, 
                                            updatedAt, ipAddress, userAgent));
    }
    
    private String validatePurpose(String purpose) {
        Objects.requireNonNull(purpose, "Purpose cannot be null");
        String trimmed = purpose.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Purpose cannot be empty");
        }
        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException("Purpose cannot exceed 1000 characters");
        }
        return trimmed;
    }
    
    private Instant calculateExpirationDate() {
        // GDPR recommends consent expiration after reasonable period
        // For marketing: 2 years, for other purposes: 3 years
        int years = consentType == ConsentType.MARKETING ? 2 : 3;
        return Instant.now().plus(years * 365, ChronoUnit.DAYS);
    }
    
    // Getters
    public ConsentId getConsentId() { return id; }
    public DataSubjectId getDataSubjectId() { return dataSubjectId; }
    public TenantId getTenantId() { return tenantId; }
    public ConsentType getConsentType() { return consentType; }
    public ConsentStatus getStatus() { return status; }
    public String getPurpose() { return purpose; }
    public Instant getGivenAt() { return givenAt; }
    public Instant getWithdrawnAt() { return withdrawnAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}