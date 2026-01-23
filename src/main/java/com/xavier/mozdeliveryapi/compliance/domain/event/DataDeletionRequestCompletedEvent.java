package com.xavier.mozdeliveryapi.compliance.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataDeletionRequestId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;

/**
 * Domain event fired when a data deletion request is completed.
 */
public record DataDeletionRequestCompletedEvent(
    DataDeletionRequestId requestId,
    DataSubjectId dataSubjectId,
    TenantId tenantId,
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
        return "DataDeletionRequestCompleted";
    }
}