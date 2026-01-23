package com.xavier.mozdeliveryapi.catalog.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event published when a catalog is created.
 */
public record CatalogCreatedEvent(
    CatalogId catalogId,
    MerchantId merchantId,
    String catalogName,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return catalogId.toString();
    }
    
    @Override
    public String getEventType() {
        return "CatalogCreated";
    }
}