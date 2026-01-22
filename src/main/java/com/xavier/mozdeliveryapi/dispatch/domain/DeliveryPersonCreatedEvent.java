package com.xavier.mozdeliveryapi.dispatch.domain;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event fired when a delivery person is created.
 */
public record DeliveryPersonCreatedEvent(
    DeliveryPersonId deliveryPersonId,
    TenantId tenantId,
    String name,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryPersonCreatedEvent {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryPersonCreatedEvent of(DeliveryPersonId deliveryPersonId, 
                                               TenantId tenantId, String name) {
        return new DeliveryPersonCreatedEvent(deliveryPersonId, tenantId, name, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return deliveryPersonId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DeliveryPersonCreated";
    }
}