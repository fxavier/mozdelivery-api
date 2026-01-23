package com.xavier.mozdeliveryapi.tenant.domain.event;


import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;

import java.time.Instant;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantStatus;

/**
 * Domain event fired when tenant status changes.
 */
public record TenantStatusChangedEvent(
    TenantId tenantId,
    TenantStatus oldStatus,
    TenantStatus newStatus,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return tenantId.toString();
    }
    
    @Override
    public String getEventType() {
        return "TenantStatusChanged";
    }
}