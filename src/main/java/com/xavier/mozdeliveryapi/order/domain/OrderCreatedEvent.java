package com.xavier.mozdeliveryapi.order.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event published when an order is created.
 */
public record OrderCreatedEvent(
    OrderId orderId,
    TenantId tenantId,
    CustomerId customerId,
    Money totalAmount,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderCreatedEvent of(OrderId orderId, TenantId tenantId, 
                                     CustomerId customerId, Money totalAmount) {
        return new OrderCreatedEvent(orderId, tenantId, customerId, totalAmount, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
    
    @Override
    public String getEventType() {
        return "OrderCreated";
    }
}