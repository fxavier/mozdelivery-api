package com.xavier.mozdeliveryapi.merchant.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantConfiguration;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event fired when merchant configuration is updated.
 */
public record MerchantConfigurationUpdatedEvent(
    MerchantId merchantId,
    MerchantConfiguration oldConfiguration,
    MerchantConfiguration newConfiguration,
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
        return "MerchantConfigurationUpdated";
    }
}