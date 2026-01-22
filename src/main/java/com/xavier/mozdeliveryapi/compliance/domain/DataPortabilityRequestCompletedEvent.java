package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event fired when a data portability request is completed.
 */
public record DataPortabilityRequestCompletedEvent(
    DataPortabilityRequestId requestId,
    DataSubjectId dataSubjectId,
    TenantId tenantId,
    String downloadUrl,
    Instant completedAt,
    Instant expiresAt
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return completedAt;
    }
    
    @Override
    public String getAggregateId() {
        return requestId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DataPortabilityRequestCompleted";
    }
}