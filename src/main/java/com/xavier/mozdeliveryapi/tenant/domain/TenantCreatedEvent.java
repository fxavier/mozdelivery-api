package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Domain event fired when a new tenant is created.
 */
public record TenantCreatedEvent(
    TenantId tenantId,
    String tenantName,
    Vertical vertical,
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
        return "TenantCreated";
    }
}