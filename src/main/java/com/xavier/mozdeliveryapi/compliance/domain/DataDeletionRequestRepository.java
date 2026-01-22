package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Repository interface for data deletion request management.
 */
public interface DataDeletionRequestRepository {
    
    /**
     * Save a data deletion request.
     */
    DataDeletionRequest save(DataDeletionRequest request);
    
    /**
     * Find request by ID.
     */
    Optional<DataDeletionRequest> findById(DataDeletionRequestId requestId);
    
    /**
     * Find all requests for a data subject.
     */
    List<DataDeletionRequest> findByDataSubjectId(DataSubjectId dataSubjectId);
    
    /**
     * Find all requests for a data subject within a tenant.
     */
    List<DataDeletionRequest> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId);
    
    /**
     * Find all pending requests that need processing.
     */
    List<DataDeletionRequest> findPendingRequests();
}