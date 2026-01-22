package com.xavier.mozdeliveryapi.compliance.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event fired when consent is given by a data subject.
 */
public record ConsentGivenEvent(
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
        return "ConsentGiven";
    }
}