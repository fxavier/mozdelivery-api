package com.xavier.mozdeliveryapi.compliance.application.dto;

import java.time.Instant;
import java.util.Map;

import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;

/**
 * Response containing audit log information.
 */
public record AuditLogResponse(
    String auditLogId,
    String tenantId,
    String userId,
    AuditEventType eventType,
    AuditSeverity severity,
    String resource,
    String action,
    String description,
    Map<String, Object> metadata,
    String ipAddress,
    String userAgent,
    String sessionId,
    Instant timestamp
) {
    
    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
            auditLog.getAuditLogId().toString(),
            auditLog.getTenantId() != null ? auditLog.getTenantId().toString() : null,
            auditLog.getUserId(),
            auditLog.getEventType(),
            auditLog.getSeverity(),
            auditLog.getResource(),
            auditLog.getAction(),
            auditLog.getDescription(),
            auditLog.getMetadata(),
            auditLog.getIpAddress(),
            auditLog.getUserAgent(),
            auditLog.getSessionId(),
            auditLog.getTimestamp()
        );
    }
}