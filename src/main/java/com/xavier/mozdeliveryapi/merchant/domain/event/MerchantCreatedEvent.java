package com.xavier.mozdeliveryapi.merchant.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;

/**
 * Domain event fired when a new merchant is created.
 */
public record MerchantCreatedEvent(
    MerchantId merchantId,
    String merchantName,
    Vertical vertical,
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
        return "MerchantCreated";
    }
}