package com.xavier.mozdeliveryapi.tenant.domain.event;


import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;

import java.time.Instant;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * Domain event fired when tenant compliance settings are updated.
 */
public record TenantComplianceUpdatedEvent(
    TenantId tenantId,
    ComplianceSettings oldSettings,
    ComplianceSettings newSettings,
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
        return "TenantComplianceUpdated";
    }
}