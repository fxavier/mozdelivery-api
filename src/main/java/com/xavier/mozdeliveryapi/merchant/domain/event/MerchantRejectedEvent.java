package com.xavier.mozdeliveryapi.merchant.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;

/**
 * Domain event fired when a merchant application is rejected.
 */
public record MerchantRejectedEvent(
    MerchantId merchantId,
    String rejectionReason,
    String rejectedBy,
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
        return "MerchantRejected";
    }
}