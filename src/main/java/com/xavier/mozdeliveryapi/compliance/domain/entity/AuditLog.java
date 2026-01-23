package com.xavier.mozdeliveryapi.compliance.domain.entity;

import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import com.xavier.mozdeliveryapi.compliance.domain.event.CriticalAuditEventOccurredEvent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditLogId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;

/**
 * Audit log aggregate for comprehensive system auditing.
 * Immutable once created to ensure audit trail integrity.
 */
public class AuditLog extends AggregateRoot<AuditLogId> {
    
    private final AuditLogId id;
    private final TenantId tenantId;
    private final String userId;
    private final AuditEventType eventType;
    private final AuditSeverity severity;
    private final String resource;
    private final String action;
    private final String description;
    private final Map<String, Object> metadata;
    private final String ipAddress;
    private final String userAgent;
    private final String sessionId;
    private final Instant timestamp;
    private final String checksum; // For integrity verification
    
    // Constructor for creating new audit log
    public AuditLog(AuditLogId id, TenantId tenantId, String userId, AuditEventType eventType,
                    AuditSeverity severity, String resource, String action, String description,
                    Map<String, Object> metadata, String ipAddress, String userAgent, String sessionId) {
        this.id = Objects.requireNonNull(id, "Audit log ID cannot be null");
        this.tenantId = tenantId; // Can be null for system-wide events
        this.userId = userId; // Can be null for system events
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.resource = validateResource(resource);
        this.action = validateAction(action);
        this.description = validateDescription(description);
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.timestamp = Instant.now();
        this.checksum = calculateChecksum();
        
        // Register domain event for critical events
        if (severity.shouldTriggerAlert()) {
            registerEvent(new CriticalAuditEventOccurredEvent(id, eventType, severity, description, timestamp));
        }
    }
    
    // Constructor for reconstituting from persistence
    public AuditLog(AuditLogId id, TenantId tenantId, String userId, AuditEventType eventType,
                    AuditSeverity severity, String resource, String action, String description,
                    Map<String, Object> metadata, String ipAddress, String userAgent, 
                    String sessionId, Instant timestamp, String checksum) {
        this.id = Objects.requireNonNull(id, "Audit log ID cannot be null");
        this.tenantId = tenantId;
        this.userId = userId;
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.resource = validateResource(resource);
        this.action = validateAction(action);
        this.description = validateDescription(description);
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.checksum = Objects.requireNonNull(checksum, "Checksum cannot be null");
    }
    
    @Override
    protected AuditLogId getId() {
        return id;
    }
    
    /**
     * Verify the integrity of this audit log entry.
     */
    public boolean verifyIntegrity() {
        return checksum.equals(calculateChecksum());
    }
    
    /**
     * Check if this audit log represents a security event.
     */
    public boolean isSecurityEvent() {
        return eventType.isSecurityEvent();
    }
    
    /**
     * Check if this audit log represents a compliance event.
     */
    public boolean isComplianceEvent() {
        return eventType.isComplianceEvent();
    }
    
    private String validateResource(String resource) {
        Objects.requireNonNull(resource, "Resource cannot be null");
        String trimmed = resource.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Resource cannot be empty");
        }
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Resource cannot exceed 255 characters");
        }
        return trimmed;
    }
    
    private String validateAction(String action) {
        Objects.requireNonNull(action, "Action cannot be null");
        String trimmed = action.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Action cannot exceed 100 characters");
        }
        return trimmed;
    }
    
    private String validateDescription(String description) {
        Objects.requireNonNull(description, "Description cannot be null");
        String trimmed = description.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException("Description cannot exceed 1000 characters");
        }
        return trimmed;
    }
    
    private String calculateChecksum() {
        // Simple checksum calculation for integrity verification
        // In production, use a proper cryptographic hash
        String data = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s", 
                                   id, eventType, severity, resource, action, 
                                   description, timestamp, userId, tenantId);
        return String.valueOf(data.hashCode());
    }
    
    // Getters
    public AuditLogId getAuditLogId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public AuditEventType getEventType() { return eventType; }
    public AuditSeverity getSeverity() { return severity; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getDescription() { return description; }
    public Map<String, Object> getMetadata() { return metadata; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getSessionId() { return sessionId; }
    public Instant getTimestamp() { return timestamp; }
    public String getChecksum() { return checksum; }
}