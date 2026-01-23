package com.xavier.mozdeliveryapi.compliance.domain.entity;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestCancelledEvent;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestCompletedEvent;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestCreatedEvent;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestExpiredEvent;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestFailedEvent;
import com.xavier.mozdeliveryapi.compliance.domain.event.DataPortabilityRequestProcessingStartedEvent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataPortabilityRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataPortabilityRequestStatus;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Data portability request aggregate for GDPR compliance.
 */
public class DataPortabilityRequest extends AggregateRoot<DataPortabilityRequestId> {
    
    private final DataPortabilityRequestId id;
    private final DataSubjectId dataSubjectId;
    private final TenantId tenantId;
    private DataPortabilityRequestStatus status;
    private final String requestedFormat; // JSON, CSV, XML
    private String downloadUrl;
    private Instant completedAt;
    private Instant expiresAt;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new request
    public DataPortabilityRequest(DataPortabilityRequestId id, DataSubjectId dataSubjectId, 
                                  TenantId tenantId, String requestedFormat) {
        this.id = Objects.requireNonNull(id, "Request ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.requestedFormat = validateFormat(requestedFormat);
        this.status = DataPortabilityRequestStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new DataPortabilityRequestCreatedEvent(id, dataSubjectId, tenantId, requestedFormat, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public DataPortabilityRequest(DataPortabilityRequestId id, DataSubjectId dataSubjectId, 
                                  TenantId tenantId, DataPortabilityRequestStatus status,
                                  String requestedFormat, String downloadUrl, Instant completedAt,
                                  Instant expiresAt, String failureReason, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Request ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.requestedFormat = validateFormat(requestedFormat);
        this.downloadUrl = downloadUrl;
        this.completedAt = completedAt;
        this.expiresAt = expiresAt;
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected DataPortabilityRequestId getId() {
        return id;
    }
    
    /**
     * Start processing the request.
     */
    public void startProcessing() {
        if (status != DataPortabilityRequestStatus.PENDING) {
            throw new IllegalStateException("Can only start processing pending requests");
        }
        
        this.status = DataPortabilityRequestStatus.PROCESSING;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataPortabilityRequestProcessingStartedEvent(id, dataSubjectId, tenantId, updatedAt));
    }
    
    /**
     * Complete the request with download URL.
     */
    public void complete(String downloadUrl) {
        if (status != DataPortabilityRequestStatus.PROCESSING) {
            throw new IllegalStateException("Can only complete processing requests");
        }
        
        Objects.requireNonNull(downloadUrl, "Download URL cannot be null");
        
        this.status = DataPortabilityRequestStatus.COMPLETED;
        this.downloadUrl = downloadUrl;
        this.completedAt = Instant.now();
        this.expiresAt = completedAt.plus(30, ChronoUnit.DAYS); // 30 days to download
        this.updatedAt = Instant.now();
        
        registerEvent(new DataPortabilityRequestCompletedEvent(id, dataSubjectId, tenantId, 
                                                              downloadUrl, completedAt, expiresAt));
    }
    
    /**
     * Fail the request with reason.
     */
    public void fail(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot fail request in final state: " + status);
        }
        
        Objects.requireNonNull(reason, "Failure reason cannot be null");
        
        this.status = DataPortabilityRequestStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataPortabilityRequestFailedEvent(id, dataSubjectId, tenantId, reason, updatedAt));
    }
    
    /**
     * Cancel the request.
     */
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException("Cannot cancel request in state: " + status);
        }
        
        this.status = DataPortabilityRequestStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataPortabilityRequestCancelledEvent(id, dataSubjectId, tenantId, updatedAt));
    }
    
    /**
     * Check if the download link has expired.
     */
    public boolean isExpired() {
        if (status != DataPortabilityRequestStatus.COMPLETED || expiresAt == null) {
            return false;
        }
        
        if (Instant.now().isAfter(expiresAt)) {
            this.status = DataPortabilityRequestStatus.EXPIRED;
            this.updatedAt = Instant.now();
            registerEvent(new DataPortabilityRequestExpiredEvent(id, dataSubjectId, tenantId, updatedAt));
            return true;
        }
        
        return false;
    }
    
    private String validateFormat(String format) {
        Objects.requireNonNull(format, "Format cannot be null");
        String trimmed = format.trim().toUpperCase();
        if (!trimmed.matches("JSON|CSV|XML")) {
            throw new IllegalArgumentException("Format must be JSON, CSV, or XML");
        }
        return trimmed;
    }
    
    // Getters
    public DataPortabilityRequestId getRequestId() { return id; }
    public DataSubjectId getDataSubjectId() { return dataSubjectId; }
    public TenantId getTenantId() { return tenantId; }
    public DataPortabilityRequestStatus getStatus() { return status; }
    public String getRequestedFormat() { return requestedFormat; }
    public String getDownloadUrl() { return downloadUrl; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}