package com.xavier.mozdeliveryapi.catalog.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event published when a product is created.
 */
public record ProductCreatedEvent(
    ProductId productId,
    MerchantId merchantId,
    CategoryId categoryId,
    String productName,
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
        return "ProductCreated";
    }
}