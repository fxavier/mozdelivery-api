package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Repository interface for data portability request management.
 */
public interface DataPortabilityRequestRepository {
    
    /**
     * Save a data portability request.
     */
    DataPortabilityRequest save(DataPortabilityRequest request);
    
    /**
     * Find request by ID.
     */
    Optional<DataPortabilityRequest> findById(DataPortabilityRequestId requestId);
    
    /**
     * Find all requests for a data subject.
     */
    List<DataPortabilityRequest> findByDataSubjectId(DataSubjectId dataSubjectId);
    
    /**
     * Find all requests for a data subject within a tenant.
     */
    List<DataPortabilityRequest> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId);
    
    /**
     * Find all pending requests that need processing.
     */
    List<DataPortabilityRequest> findPendingRequests();
    
    /**
     * Find all completed requests that have expired download links.
     */
    List<DataPortabilityRequest> findExpiredRequests();
}