package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Domain event fired when a tenant is suspended.
 */
public record TenantSuspendedEvent(
    TenantId tenantId,
    String reason,
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
        return "TenantSuspended";
    }
}