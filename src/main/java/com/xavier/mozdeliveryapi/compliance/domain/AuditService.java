package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain service for audit logging operations.
 */
public interface AuditService {
    
    /**
     * Log an audit event.
     */
    AuditLog logEvent(TenantId tenantId, String userId, AuditEventType eventType, 
                     AuditSeverity severity, String resource, String action, String description,
                     Map<String, Object> metadata, String ipAddress, String userAgent, String sessionId);
    
    /**
     * Log a security event.
     */
    AuditLog logSecurityEvent(TenantId tenantId, String userId, AuditEventType eventType,
                             String resource, String description, Map<String, Object> metadata,
                             String ipAddress, String userAgent, String sessionId);
    
    /**
     * Log a compliance event.
     */
    AuditLog logComplianceEvent(TenantId tenantId, String userId, AuditEventType eventType,
                               String resource, String description, Map<String, Object> metadata,
                               String ipAddress, String userAgent, String sessionId);
    
    /**
     * Get audit logs for a tenant within a time range.
     */
    List<AuditLog> getAuditLogsForTenant(TenantId tenantId, Instant startTime, Instant endTime);
    
    /**
     * Get audit logs for a user within a time range.
     */
    List<AuditLog> getAuditLogsForUser(String userId, Instant startTime, Instant endTime);
    
    /**
     * Get security events within a time range.
     */
    List<AuditLog> getSecurityEvents(Instant startTime, Instant endTime);
    
    /**
     * Get compliance events within a time range.
     */
    List<AuditLog> getComplianceEvents(Instant startTime, Instant endTime);
    
    /**
     * Verify audit log integrity.
     */
    List<AuditLog> verifyAuditLogIntegrity();
    
    /**
     * Generate audit report for a tenant.
     */
    Map<String, Object> generateAuditReport(TenantId tenantId, Instant startTime, Instant endTime);
}