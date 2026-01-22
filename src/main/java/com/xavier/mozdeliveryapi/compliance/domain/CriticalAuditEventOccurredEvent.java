package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a critical audit event occurs.
 */
public record CriticalAuditEventOccurredEvent(
    AuditLogId auditLogId,
    AuditEventType eventType,
    AuditSeverity severity,
    String description,
    Instant occurredOn
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return auditLogId.toString();
    }
    
    @Override
    public String getEventType() {
        return "CriticalAuditEventOccurred";
    }
}