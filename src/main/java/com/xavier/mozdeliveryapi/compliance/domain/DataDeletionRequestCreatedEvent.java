package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event fired when a data deletion request is created.
 */
public record DataDeletionRequestCreatedEvent(
    DataDeletionRequestId requestId,
    DataSubjectId dataSubjectId,
    TenantId tenantId,
    String reason,
    Instant occurredOn
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return requestId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DataDeletionRequestCreated";
    }
}