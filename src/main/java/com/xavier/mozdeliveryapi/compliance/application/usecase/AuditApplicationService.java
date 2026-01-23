package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import com.xavier.mozdeliveryapi.compliance.application.dto.AuditLogResponse;

/**
 * Application service for audit operations.
 */
public interface AuditApplicationService {
    
    /**
     * Get audit logs for a tenant within a time range.
     */
    List<AuditLogResponse> getAuditLogsForTenant(String tenantId, Instant startTime, Instant endTime);
    
    /**
     * Get audit logs for a user within a time range.
     */
    List<AuditLogResponse> getAuditLogsForUser(String userId, Instant startTime, Instant endTime);
    
    /**
     * Get security events within a time range.
     */
    List<AuditLogResponse> getSecurityEvents(Instant startTime, Instant endTime);
    
    /**
     * Get compliance events within a time range.
     */
    List<AuditLogResponse> getComplianceEvents(Instant startTime, Instant endTime);
    
    /**
     * Generate audit report for a tenant.
     */
    Map<String, Object> generateAuditReport(String tenantId, Instant startTime, Instant endTime);
    
    /**
     * Verify audit log integrity.
     */
    List<String> verifyAuditLogIntegrity();
}