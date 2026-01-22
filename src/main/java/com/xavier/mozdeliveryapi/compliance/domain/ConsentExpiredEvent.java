package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event fired when consent expires.
 */
public record ConsentExpiredEvent(
    ConsentId consentId,
    DataSubjectId dataSubjectId,
    TenantId tenantId,
    ConsentType consentType,
    Instant occurredOn
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return consentId.toString();
    }
    
    @Override
    public String getEventType() {
        return "ConsentExpired";
    }
}