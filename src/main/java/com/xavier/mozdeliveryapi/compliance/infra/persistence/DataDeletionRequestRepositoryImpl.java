package com.xavier.mozdeliveryapi.compliance.infra.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.compliance.domain.entity.DataDeletionRequest;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataDeletionRequestId;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.DataDeletionRequestRepository;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataDeletionRequestStatus;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * In-memory implementation of data deletion request repository for development/testing.
 * In production, this would be replaced with a JPA implementation.
 */
@Repository
public class DataDeletionRequestRepositoryImpl implements DataDeletionRequestRepository {
    
    private final Map<DataDeletionRequestId, DataDeletionRequest> requests = new ConcurrentHashMap<>();
    
    @Override
    public DataDeletionRequest save(DataDeletionRequest request) {
        requests.put(request.getRequestId(), request);
        return request;
    }
    
    @Override
    public Optional<DataDeletionRequest> findById(DataDeletionRequestId requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }
    
    @Override
    public List<DataDeletionRequest> findByDataSubjectId(DataSubjectId dataSubjectId) {
        return requests.values().stream()
                      .filter(request -> request.getDataSubjectId().equals(dataSubjectId))
                      .toList();
    }
    
    @Override
    public List<DataDeletionRequest> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId) {
        return requests.values().stream()
                      .filter(request -> request.getDataSubjectId().equals(dataSubjectId) &&
                                       request.getTenantId().equals(tenantId))
                      .toList();
    }
    
    @Override
    public List<DataDeletionRequest> findPendingRequests() {
        return requests.values().stream()
                      .filter(request -> request.getStatus() == DataDeletionRequestStatus.PENDING)
                      .toList();
    }
}