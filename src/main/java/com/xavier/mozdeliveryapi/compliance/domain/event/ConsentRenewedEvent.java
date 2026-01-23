package com.xavier.mozdeliveryapi.compliance.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.DataSubjectId;

/**
 * Domain event fired when consent is renewed.
 */
public record ConsentRenewedEvent(
    ConsentId consentId,
    DataSubjectId dataSubjectId,
    TenantId tenantId,
    ConsentType consentType,
    Instant occurredOn,
    String ipAddress,
    String userAgent
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
        return "ConsentRenewed";
    }
}