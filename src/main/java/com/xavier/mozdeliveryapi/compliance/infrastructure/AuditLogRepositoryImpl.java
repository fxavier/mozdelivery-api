package com.xavier.mozdeliveryapi.compliance.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.compliance.domain.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.AuditLogId;
import com.xavier.mozdeliveryapi.compliance.domain.AuditLogRepository;
import com.xavier.mozdeliveryapi.compliance.domain.AuditSeverity;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * In-memory implementation of audit log repository for development/testing.
 * In production, this would be replaced with a JPA implementation using immutable storage.
 */
@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {
    
    private final Map<AuditLogId, AuditLog> auditLogs = new ConcurrentHashMap<>();
    
    @Override
    public AuditLog save(AuditLog auditLog) {
        auditLogs.put(auditLog.getAuditLogId(), auditLog);
        return auditLog;
    }
    
    @Override
    public Optional<AuditLog> findById(AuditLogId auditLogId) {
        return Optional.ofNullable(auditLogs.get(auditLogId));
    }
    
    @Override
    public List<AuditLog> findByTenantIdAndTimestampBetween(TenantId tenantId, Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> tenantId.equals(log.getTenantId()) &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findByUserIdAndTimestampBetween(String userId, Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> userId.equals(log.getUserId()) &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findByEventTypeAndTimestampBetween(AuditEventType eventType, Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> eventType.equals(log.getEventType()) &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findBySeverityAndTimestampBetween(AuditSeverity severity, Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> severity.equals(log.getSeverity()) &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findSecurityEventsInTimeRange(Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> log.isSecurityEvent() &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findComplianceEventsInTimeRange(Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> log.isComplianceEvent() &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .toList();
    }
    
    @Override
    public List<AuditLog> findLogsWithIntegrityIssues() {
        return auditLogs.values().stream()
                        .filter(log -> !log.verifyIntegrity())
                        .toList();
    }
    
    @Override
    public long countByEventTypeAndTimestampBetween(AuditEventType eventType, Instant startTime, Instant endTime) {
        return auditLogs.values().stream()
                        .filter(log -> eventType.equals(log.getEventType()) &&
                                     !log.getTimestamp().isBefore(startTime) &&
                                     !log.getTimestamp().isAfter(endTime))
                        .count();
    }
}