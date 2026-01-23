package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.application.usecase.AuditService;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.application.dto.AuditLogResponse;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Implementation of audit application service.
 */
@Service
@Transactional(readOnly = true)
public class AuditApplicationServiceImpl implements AuditApplicationService {
    
    private final AuditService auditService;
    
    public AuditApplicationServiceImpl(AuditService auditService) {
        this.auditService = Objects.requireNonNull(auditService);
    }
    
    @Override
    public List<AuditLogResponse> getAuditLogsForTenant(String tenantId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        TenantId tenant = TenantId.of(tenantId);
        List<AuditLog> auditLogs = auditService.getAuditLogsForTenant(tenant, startTime, endTime);
        
        return auditLogs.stream()
                       .map(AuditLogResponse::from)
                       .toList();
    }
    
    @Override
    public List<AuditLogResponse> getAuditLogsForUser(String userId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        List<AuditLog> auditLogs = auditService.getAuditLogsForUser(userId, startTime, endTime);
        
        return auditLogs.stream()
                       .map(AuditLogResponse::from)
                       .toList();
    }
    
    @Override
    public List<AuditLogResponse> getSecurityEvents(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        List<AuditLog> auditLogs = auditService.getSecurityEvents(startTime, endTime);
        
        return auditLogs.stream()
                       .map(AuditLogResponse::from)
                       .toList();
    }
    
    @Override
    public List<AuditLogResponse> getComplianceEvents(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        List<AuditLog> auditLogs = auditService.getComplianceEvents(startTime, endTime);
        
        return auditLogs.stream()
                       .map(AuditLogResponse::from)
                       .toList();
    }
    
    @Override
    public Map<String, Object> generateAuditReport(String tenantId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        TenantId tenant = TenantId.of(tenantId);
        return auditService.generateAuditReport(tenant, startTime, endTime);
    }
    
    @Override
    public List<String> verifyAuditLogIntegrity() {
        List<AuditLog> logsWithIssues = auditService.verifyAuditLogIntegrity();
        
        return logsWithIssues.stream()
                            .map(log -> log.getAuditLogId().toString())
                            .toList();
    }
}