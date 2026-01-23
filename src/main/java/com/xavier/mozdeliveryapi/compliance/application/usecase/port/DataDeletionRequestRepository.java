package com.xavier.mozdeliveryapi.compliance.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.DataDeletionRequest;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataDeletionRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;

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