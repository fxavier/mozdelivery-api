package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditLogId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.compliance.application.usecase.port.AuditLogRepository;

/**
 * Implementation of audit service.
 */
@Service
public class AuditServiceImpl implements AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = Objects.requireNonNull(auditLogRepository);
    }
    
    @Override
    public AuditLog logEvent(TenantId tenantId, String userId, AuditEventType eventType,
                            AuditSeverity severity, String resource, String action, String description,
                            Map<String, Object> metadata, String ipAddress, String userAgent, String sessionId) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(severity, "Severity cannot be null");
        Objects.requireNonNull(resource, "Resource cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        
        AuditLog auditLog = new AuditLog(AuditLogId.generate(), tenantId, userId, eventType,
                                        severity, resource, action, description, metadata,
                                        ipAddress, userAgent, sessionId);
        
        return auditLogRepository.save(auditLog);
    }
    
    @Override
    public AuditLog logSecurityEvent(TenantId tenantId, String userId, AuditEventType eventType,
                                    String resource, String description, Map<String, Object> metadata,
                                    String ipAddress, String userAgent, String sessionId) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        
        if (!eventType.isSecurityEvent()) {
            throw new IllegalArgumentException("Event type must be a security event: " + eventType);
        }
        
        AuditSeverity severity = eventType == AuditEventType.SECURITY_BREACH ? 
                                AuditSeverity.CRITICAL : AuditSeverity.WARNING;
        
        return logEvent(tenantId, userId, eventType, severity, resource, "SECURITY_EVENT", 
                       description, metadata, ipAddress, userAgent, sessionId);
    }
    
    @Override
    public AuditLog logComplianceEvent(TenantId tenantId, String userId, AuditEventType eventType,
                                      String resource, String description, Map<String, Object> metadata,
                                      String ipAddress, String userAgent, String sessionId) {
        Objects.requireNonNull(eventType, "Event type cannot be null");
        
        if (!eventType.isComplianceEvent()) {
            throw new IllegalArgumentException("Event type must be a compliance event: " + eventType);
        }
        
        return logEvent(tenantId, userId, eventType, AuditSeverity.INFO, resource, "COMPLIANCE_EVENT",
                       description, metadata, ipAddress, userAgent, sessionId);
    }
    
    @Override
    public List<AuditLog> getAuditLogsForTenant(TenantId tenantId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        return auditLogRepository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime);
    }
    
    @Override
    public List<AuditLog> getAuditLogsForUser(String userId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        return auditLogRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime);
    }
    
    @Override
    public List<AuditLog> getSecurityEvents(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        return auditLogRepository.findSecurityEventsInTimeRange(startTime, endTime);
    }
    
    @Override
    public List<AuditLog> getComplianceEvents(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        return auditLogRepository.findComplianceEventsInTimeRange(startTime, endTime);
    }
    
    @Override
    public List<AuditLog> verifyAuditLogIntegrity() {
        return auditLogRepository.findLogsWithIntegrityIssues();
    }
    
    @Override
    public Map<String, Object> generateAuditReport(TenantId tenantId, Instant startTime, Instant endTime) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        List<AuditLog> auditLogs = getAuditLogsForTenant(tenantId, startTime, endTime);
        
        Map<String, Object> report = new HashMap<>();
        report.put("tenant_id", tenantId.toString());
        report.put("start_time", startTime);
        report.put("end_time", endTime);
        report.put("total_events", auditLogs.size());
        
        // Group by event type
        Map<AuditEventType, Long> eventTypeCounts = auditLogs.stream()
            .collect(Collectors.groupingBy(AuditLog::getEventType, Collectors.counting()));
        report.put("events_by_type", eventTypeCounts);
        
        // Group by severity
        Map<AuditSeverity, Long> severityCounts = auditLogs.stream()
            .collect(Collectors.groupingBy(AuditLog::getSeverity, Collectors.counting()));
        report.put("events_by_severity", severityCounts);
        
        // Security events
        long securityEvents = auditLogs.stream()
            .mapToLong(log -> log.isSecurityEvent() ? 1 : 0)
            .sum();
        report.put("security_events", securityEvents);
        
        // Compliance events
        long complianceEvents = auditLogs.stream()
            .mapToLong(log -> log.isComplianceEvent() ? 1 : 0)
            .sum();
        report.put("compliance_events", complianceEvents);
        
        // Critical events
        long criticalEvents = auditLogs.stream()
            .mapToLong(log -> log.getSeverity() == AuditSeverity.CRITICAL ? 1 : 0)
            .sum();
        report.put("critical_events", criticalEvents);
        
        return report;
    }
}
