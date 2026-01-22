package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Domain event fired when tenant configuration is updated.
 */
public record TenantConfigurationUpdatedEvent(
    TenantId tenantId,
    TenantConfiguration oldConfiguration,
    TenantConfiguration newConfiguration,
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
        return "TenantConfigurationUpdated";
    }
}