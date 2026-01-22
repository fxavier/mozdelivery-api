package com.xavier.mozdeliveryapi.compliance.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.compliance.domain.DataPortabilityRequest;
import com.xavier.mozdeliveryapi.compliance.domain.DataPortabilityRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.DataPortabilityRequestRepository;
import com.xavier.mozdeliveryapi.compliance.domain.DataPortabilityRequestStatus;
import com.xavier.mozdeliveryapi.compliance.domain.DataSubjectId;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * In-memory implementation of data portability request repository for development/testing.
 * In production, this would be replaced with a JPA implementation.
 */
@Repository
public class DataPortabilityRequestRepositoryImpl implements DataPortabilityRequestRepository {
    
    private final Map<DataPortabilityRequestId, DataPortabilityRequest> requests = new ConcurrentHashMap<>();
    
    @Override
    public DataPortabilityRequest save(DataPortabilityRequest request) {
        requests.put(request.getRequestId(), request);
        return request;
    }
    
    @Override
    public Optional<DataPortabilityRequest> findById(DataPortabilityRequestId requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }
    
    @Override
    public List<DataPortabilityRequest> findByDataSubjectId(DataSubjectId dataSubjectId) {
        return requests.values().stream()
                      .filter(request -> request.getDataSubjectId().equals(dataSubjectId))
                      .toList();
    }
    
    @Override
    public List<DataPortabilityRequest> findByDataSubjectIdAndTenantId(DataSubjectId dataSubjectId, TenantId tenantId) {
        return requests.values().stream()
                      .filter(request -> request.getDataSubjectId().equals(dataSubjectId) &&
                                       request.getTenantId().equals(tenantId))
                      .toList();
    }
    
    @Override
    public List<DataPortabilityRequest> findPendingRequests() {
        return requests.values().stream()
                      .filter(request -> request.getStatus() == DataPortabilityRequestStatus.PENDING)
                      .toList();
    }
    
    @Override
    public List<DataPortabilityRequest> findExpiredRequests() {
        Instant now = Instant.now();
        return requests.values().stream()
                      .filter(request -> request.getStatus() == DataPortabilityRequestStatus.COMPLETED &&
                                       request.getExpiresAt() != null &&
                                       now.isAfter(request.getExpiresAt()))
                      .toList();
    }
}