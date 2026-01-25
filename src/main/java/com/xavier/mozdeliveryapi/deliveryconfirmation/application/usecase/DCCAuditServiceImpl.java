package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.compliance.application.usecase.AuditService;
import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * Implementation of DCC audit service.
 * 
 * Provides comprehensive audit logging for all DCC operations with security
 * monitoring and compliance tracking.
 */
@Service
@Transactional
public class DCCAuditServiceImpl implements DCCAuditService {
    
    private final AuditService auditService;
    
    // Configuration constants
    private static final int GLOBAL_ATTEMPT_LIMIT = 20; // Max attempts per courier per hour
    private static final long GLOBAL_ATTEMPT_WINDOW_MINUTES = 60;
    
    public DCCAuditServiceImpl(AuditService auditService) {
        this.auditService = Objects.requireNonNull(auditService, "Audit service cannot be null");
    }
    
    @Override
    public void logDCCGenerated(OrderId orderId, MerchantId merchantId, String code, 
                               Instant expiresAt, String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("codeLength", code.length());
        metadata.put("expiresAt", expiresAt.toString());
        metadata.put("validityMinutes", java.time.Duration.between(Instant.now(), expiresAt).toMinutes());
        
        auditService.logComplianceEvent(
            TenantId.of(merchantId.value()),
            "SYSTEM", // System-generated
            AuditEventType.DCC_GENERATED,
            "DCC",
            String.format("DCC generated for order %s, expires at %s", orderId.value(), expiresAt),
            metadata,
            ipAddress,
            null, // No user agent for system events
            sessionId
        );
    }
    
    @Override
    public void logDCCValidated(OrderId orderId, MerchantId merchantId, String courierId, 
                               String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("courierId", courierId);
        metadata.put("validatedAt", Instant.now().toString());
        
        auditService.logComplianceEvent(
            TenantId.of(merchantId.value()),
            courierId,
            AuditEventType.DCC_VALIDATED,
            "DCC",
            String.format("DCC successfully validated for order %s by courier %s", orderId.value(), courierId),
            metadata,
            ipAddress,
            null,
            sessionId
        );
    }
    
    @Override
    public void logDCCValidationFailed(OrderId orderId, MerchantId merchantId, String courierId, 
                                      String attemptedCode, int attemptNumber, int maxAttempts,
                                      String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(attemptedCode, "Attempted code cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("courierId", courierId);
        metadata.put("attemptedCodeLength", attemptedCode.length());
        metadata.put("attemptNumber", attemptNumber);
        metadata.put("maxAttempts", maxAttempts);
        metadata.put("remainingAttempts", maxAttempts - attemptNumber);
        metadata.put("isFinalAttempt", attemptNumber >= maxAttempts);
        
        // Determine severity based on attempt number
        AuditSeverity severity = attemptNumber >= maxAttempts ? AuditSeverity.ERROR : AuditSeverity.WARNING;
        
        auditService.logSecurityEvent(
            TenantId.of(merchantId.value()),
            courierId,
            AuditEventType.DCC_VALIDATION_FAILED,
            "DCC",
            String.format("DCC validation failed for order %s by courier %s (attempt %d/%d)", 
                         orderId.value(), courierId, attemptNumber, maxAttempts),
            metadata,
            ipAddress,
            null,
            sessionId
        );
        
        // Log lockout if this was the final attempt
        if (attemptNumber >= maxAttempts) {
            logDCCLockout(orderId, merchantId, courierId, attemptNumber, sessionId, ipAddress);
        }
    }
    
    @Override
    public void logDCCExpired(OrderId orderId, MerchantId merchantId, boolean isForced, 
                             String adminId, String reason, String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("isForced", isForced);
        metadata.put("expiredAt", Instant.now().toString());
        
        if (isForced) {
            metadata.put("adminId", adminId);
            metadata.put("reason", reason);
        }
        
        AuditEventType eventType = isForced ? AuditEventType.DCC_FORCE_EXPIRED : AuditEventType.DCC_EXPIRED;
        String userId = isForced ? adminId : "SYSTEM";
        String description = isForced 
            ? String.format("DCC force expired for order %s by admin %s. Reason: %s", orderId.value(), adminId, reason)
            : String.format("DCC expired for order %s", orderId.value());
        
        auditService.logComplianceEvent(
            TenantId.of(merchantId.value()),
            userId,
            eventType,
            "DCC",
            description,
            metadata,
            ipAddress,
            null,
            sessionId
        );
    }
    
    @Override
    public void logDCCLockout(OrderId orderId, MerchantId merchantId, String courierId, 
                             int totalAttempts, String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("courierId", courierId);
        metadata.put("totalAttempts", totalAttempts);
        metadata.put("lockedOutAt", Instant.now().toString());
        
        auditService.logSecurityEvent(
            TenantId.of(merchantId.value()),
            courierId,
            AuditEventType.DCC_LOCKOUT_TRIGGERED,
            "DCC",
            String.format("DCC lockout triggered for order %s - courier %s exceeded maximum attempts (%d)", 
                         orderId.value(), courierId, totalAttempts),
            metadata,
            ipAddress,
            null,
            sessionId
        );
    }
    
    @Override
    public void logDCCResent(OrderId orderId, MerchantId merchantId, String newCode, 
                            String reason, String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(newCode, "New code cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("newCodeLength", newCode.length());
        metadata.put("reason", reason != null ? reason : "User requested");
        metadata.put("resentAt", Instant.now().toString());
        
        auditService.logComplianceEvent(
            TenantId.of(merchantId.value()),
            "SYSTEM", // System-generated
            AuditEventType.DCC_RESENT,
            "DCC",
            String.format("DCC resent for order %s. Reason: %s", orderId.value(), 
                         reason != null ? reason : "User requested"),
            metadata,
            ipAddress,
            null,
            sessionId
        );
    }
    
    @Override
    public void logSuspiciousActivity(OrderId orderId, MerchantId merchantId, String courierId,
                                     String activityType, String details, String sessionId, String ipAddress) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(activityType, "Activity type cannot be null");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId.value().toString());
        metadata.put("courierId", courierId);
        metadata.put("activityType", activityType);
        metadata.put("details", details != null ? details : "");
        metadata.put("detectedAt", Instant.now().toString());
        
        auditService.logSecurityEvent(
            TenantId.of(merchantId.value()),
            courierId,
            AuditEventType.SUSPICIOUS_ACTIVITY,
            "DCC",
            String.format("Suspicious DCC activity detected for order %s by courier %s: %s", 
                         orderId.value(), courierId, activityType),
            metadata,
            ipAddress,
            null,
            sessionId
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasCourierExceededGlobalAttempts(String courierId, Instant timeWindow) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(timeWindow, "Time window cannot be null");
        
        // Get all DCC validation failed events for this courier within the time window
        List<AuditLog> failedAttempts = auditService.getSecurityEvents(timeWindow, Instant.now())
            .stream()
            .filter(log -> AuditEventType.DCC_VALIDATION_FAILED.equals(log.getEventType()))
            .filter(log -> courierId.equals(log.getUserId()))
            .toList();
        
        return failedAttempts.size() >= GLOBAL_ATTEMPT_LIMIT;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDCCAuditTrail(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // Get all audit logs related to this order
        // Note: This is a simplified implementation. In a real system, you'd want to
        // query by metadata or have a more sophisticated search mechanism
        Instant oneDayAgo = Instant.now().minus(java.time.Duration.ofDays(1));
        List<AuditLog> allLogs = auditService.getSecurityEvents(oneDayAgo, Instant.now());
        
        List<AuditLog> orderLogs = allLogs.stream()
            .filter(log -> log.getMetadata().containsKey("orderId"))
            .filter(log -> orderId.value().toString().equals(log.getMetadata().get("orderId")))
            .toList();
        
        Map<String, Object> auditTrail = new HashMap<>();
        auditTrail.put("orderId", orderId.value().toString());
        auditTrail.put("totalEvents", orderLogs.size());
        auditTrail.put("events", orderLogs.stream()
            .map(log -> Map.of(
                "timestamp", log.getTimestamp(),
                "eventType", log.getEventType(),
                "severity", log.getSeverity(),
                "description", log.getDescription(),
                "userId", log.getUserId(),
                "metadata", log.getMetadata()
            ))
            .toList());
        
        return auditTrail;
    }
}