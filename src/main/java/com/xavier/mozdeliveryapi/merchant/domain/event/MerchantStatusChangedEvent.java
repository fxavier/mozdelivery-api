package com.xavier.mozdeliveryapi.merchant.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event fired when merchant status changes.
 */
public record MerchantStatusChangedEvent(
    MerchantId merchantId,
    MerchantStatus oldStatus,
    MerchantStatus newStatus,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return merchantId.toString();
    }
    
    @Override
    public String getEventType() {
        return "MerchantStatusChanged";
    }
}