package com.xavier.mozdeliveryapi.compliance.application.usecase.port;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditLogId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;

/**
 * Repository interface for audit log management.
 */
public interface AuditLogRepository {
    
    /**
     * Save an audit log entry.
     */
    AuditLog save(AuditLog auditLog);
    
    /**
     * Find audit log by ID.
     */
    Optional<AuditLog> findById(AuditLogId auditLogId);
    
    /**
     * Find audit logs by tenant within a time range.
     */
    List<AuditLog> findByTenantIdAndTimestampBetween(TenantId tenantId, Instant startTime, Instant endTime);
    
    /**
     * Find audit logs by user within a time range.
     */
    List<AuditLog> findByUserIdAndTimestampBetween(String userId, Instant startTime, Instant endTime);
    
    /**
     * Find audit logs by event type within a time range.
     */
    List<AuditLog> findByEventTypeAndTimestampBetween(AuditEventType eventType, Instant startTime, Instant endTime);
    
    /**
     * Find audit logs by severity within a time range.
     */
    List<AuditLog> findBySeverityAndTimestampBetween(AuditSeverity severity, Instant startTime, Instant endTime);
    
    /**
     * Find security-related audit logs within a time range.
     */
    List<AuditLog> findSecurityEventsInTimeRange(Instant startTime, Instant endTime);
    
    /**
     * Find compliance-related audit logs within a time range.
     */
    List<AuditLog> findComplianceEventsInTimeRange(Instant startTime, Instant endTime);
    
    /**
     * Find audit logs with integrity issues.
     */
    List<AuditLog> findLogsWithIntegrityIssues();
    
    /**
     * Count audit logs by event type within a time range.
     */
    long countByEventTypeAndTimestampBetween(AuditEventType eventType, Instant startTime, Instant endTime);
}