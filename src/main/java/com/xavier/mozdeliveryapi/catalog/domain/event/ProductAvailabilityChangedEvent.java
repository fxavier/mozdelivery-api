package com.xavier.mozdeliveryapi.catalog.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event published when a product's availability changes.
 */
public record ProductAvailabilityChangedEvent(
    ProductId productId,
    MerchantId merchantId,
    ProductAvailability previousAvailability,
    ProductAvailability newAvailability,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return productId.toString();
    }
    
    @Override
    public String getEventType() {
        return "ProductAvailabilityChanged";
    }
}