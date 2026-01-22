package com.xavier.mozdeliveryapi.compliance.domain;

import com.xavier.mozdeliveryapi.shared.domain.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.time.Instant;
import java.util.Objects;

/**
 * Data deletion request aggregate for GDPR compliance.
 */
public class DataDeletionRequest extends AggregateRoot<DataDeletionRequestId> {
    
    private final DataDeletionRequestId id;
    private final DataSubjectId dataSubjectId;
    private final TenantId tenantId;
    private DataDeletionRequestStatus status;
    private final String reason;
    private Instant completedAt;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new request
    public DataDeletionRequest(DataDeletionRequestId id, DataSubjectId dataSubjectId, 
                               TenantId tenantId, String reason) {
        this.id = Objects.requireNonNull(id, "Request ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.reason = validateReason(reason);
        this.status = DataDeletionRequestStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new DataDeletionRequestCreatedEvent(id, dataSubjectId, tenantId, reason, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public DataDeletionRequest(DataDeletionRequestId id, DataSubjectId dataSubjectId, 
                               TenantId tenantId, DataDeletionRequestStatus status,
                               String reason, Instant completedAt, String failureReason,
                               Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Request ID cannot be null");
        this.dataSubjectId = Objects.requireNonNull(dataSubjectId, "Data subject ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.reason = validateReason(reason);
        this.completedAt = completedAt;
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected DataDeletionRequestId getId() {
        return id;
    }
    
    /**
     * Start processing the request.
     */
    public void startProcessing() {
        if (status != DataDeletionRequestStatus.PENDING) {
            throw new IllegalStateException("Can only start processing pending requests");
        }
        
        this.status = DataDeletionRequestStatus.PROCESSING;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataDeletionRequestProcessingStartedEvent(id, dataSubjectId, tenantId, updatedAt));
    }
    
    /**
     * Complete the request.
     */
    public void complete() {
        if (status != DataDeletionRequestStatus.PROCESSING) {
            throw new IllegalStateException("Can only complete processing requests");
        }
        
        this.status = DataDeletionRequestStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
        
        registerEvent(new DataDeletionRequestCompletedEvent(id, dataSubjectId, tenantId, completedAt));
    }
    
    /**
     * Fail the request with reason.
     */
    public void fail(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot fail request in final state: " + status);
        }
        
        Objects.requireNonNull(reason, "Failure reason cannot be null");
        
        this.status = DataDeletionRequestStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataDeletionRequestFailedEvent(id, dataSubjectId, tenantId, reason, updatedAt));
    }
    
    /**
     * Cancel the request.
     */
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException("Cannot cancel request in state: " + status);
        }
        
        this.status = DataDeletionRequestStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        registerEvent(new DataDeletionRequestCancelledEvent(id, dataSubjectId, tenantId, updatedAt));
    }
    
    private String validateReason(String reason) {
        Objects.requireNonNull(reason, "Reason cannot be null");
        String trimmed = reason.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters");
        }
        return trimmed;
    }
    
    // Getters
    public DataDeletionRequestId getRequestId() { return id; }
    public DataSubjectId getDataSubjectId() { return dataSubjectId; }
    public TenantId getTenantId() { return tenantId; }
    public DataDeletionRequestStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public Instant getCompletedAt() { return completedAt; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}