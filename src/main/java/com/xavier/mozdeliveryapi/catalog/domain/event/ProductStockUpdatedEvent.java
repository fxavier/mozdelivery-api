package com.xavier.mozdeliveryapi.catalog.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain event published when a product's stock is updated.
 */
public record ProductStockUpdatedEvent(
    ProductId productId,
    MerchantId merchantId,
    StockInfo previousStockInfo,
    StockInfo newStockInfo,
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
        return "ProductStockUpdated";
    }
}