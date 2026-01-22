package com.xavier.mozdeliveryapi.compliance.infrastructure.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.compliance.application.AuditApplicationService;
import com.xavier.mozdeliveryapi.compliance.application.AuditLogResponse;
import com.xavier.mozdeliveryapi.shared.infrastructure.multitenant.TenantContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for audit operations.
 */
@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit", description = "Audit trail and logging operations")
@CrossOrigin(origins = "*")
public class AuditController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);
    
    private final AuditApplicationService auditApplicationService;
    
    public AuditController(AuditApplicationService auditApplicationService) {
        this.auditApplicationService = auditApplicationService;
    }
    
    @Operation(summary = "Get audit logs for tenant", description = "Retrieves audit logs for the authenticated tenant within a time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/logs/tenant")
    @PreAuthorize("hasAuthority('SCOPE_audit:read')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsForTenant(
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting audit logs for tenant: {} from {} to {}", tenantId, startTime, endTime);
        
        try {
            List<AuditLogResponse> auditLogs = auditApplicationService
                .getAuditLogsForTenant(tenantId, startTime, endTime);
            
            return ResponseEntity.ok(auditLogs);
            
        } catch (Exception e) {
            logger.error("Error getting audit logs for tenant: {} from {} to {}", 
                tenantId, startTime, endTime, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Get audit logs for user", description = "Retrieves audit logs for a specific user within a time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/logs/user/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_audit:read')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsForUser(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        logger.info("Getting audit logs for user: {} from {} to {}", userId, startTime, endTime);
        
        try {
            List<AuditLogResponse> auditLogs = auditApplicationService
                .getAuditLogsForUser(userId, startTime, endTime);
            
            return ResponseEntity.ok(auditLogs);
            
        } catch (Exception e) {
            logger.error("Error getting audit logs for user: {} from {} to {}", 
                userId, startTime, endTime, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Get security events", description = "Retrieves security-related audit events within a time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Security events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/logs/security")
    @PreAuthorize("hasAuthority('SCOPE_audit:read')")
    public ResponseEntity<List<AuditLogResponse>> getSecurityEvents(
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        logger.info("Getting security events from {} to {}", startTime, endTime);
        
        try {
            List<AuditLogResponse> securityEvents = auditApplicationService
                .getSecurityEvents(startTime, endTime);
            
            return ResponseEntity.ok(securityEvents);
            
        } catch (Exception e) {
            logger.error("Error getting security events from {} to {}", startTime, endTime, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Get compliance events", description = "Retrieves compliance-related audit events within a time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compliance events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/logs/compliance")
    @PreAuthorize("hasAuthority('SCOPE_audit:read')")
    public ResponseEntity<List<AuditLogResponse>> getComplianceEvents(
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        logger.info("Getting compliance events from {} to {}", startTime, endTime);
        
        try {
            List<AuditLogResponse> complianceEvents = auditApplicationService
                .getComplianceEvents(startTime, endTime);
            
            return ResponseEntity.ok(complianceEvents);
            
        } catch (Exception e) {
            logger.error("Error getting compliance events from {} to {}", startTime, endTime, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Generate audit report", description = "Generates a comprehensive audit report for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit report generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/report")
    @PreAuthorize("hasAuthority('SCOPE_audit:read')")
    public ResponseEntity<Map<String, Object>> generateAuditReport(
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Generating audit report for tenant: {} from {} to {}", tenantId, startTime, endTime);
        
        try {
            Map<String, Object> auditReport = auditApplicationService
                .generateAuditReport(tenantId, startTime, endTime);
            
            return ResponseEntity.ok(auditReport);
            
        } catch (Exception e) {
            logger.error("Error generating audit report for tenant: {} from {} to {}", 
                tenantId, startTime, endTime, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Verify audit log integrity", description = "Verifies the integrity of audit logs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit log integrity verification completed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/verify-integrity")
    @PreAuthorize("hasAuthority('SCOPE_audit:admin')")
    public ResponseEntity<List<String>> verifyAuditLogIntegrity() {
        
        logger.info("Verifying audit log integrity");
        
        try {
            List<String> integrityIssues = auditApplicationService.verifyAuditLogIntegrity();
            
            if (integrityIssues.isEmpty()) {
                logger.info("Audit log integrity verification passed");
            } else {
                logger.warn("Audit log integrity issues found: {}", integrityIssues.size());
            }
            
            return ResponseEntity.ok(integrityIssues);
            
        } catch (Exception e) {
            logger.error("Error verifying audit log integrity", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}