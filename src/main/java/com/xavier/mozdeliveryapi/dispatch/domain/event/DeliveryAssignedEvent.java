package com.xavier.mozdeliveryapi.dispatch.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Domain event fired when a delivery is assigned to a delivery person.
 */
public record DeliveryAssignedEvent(
    DeliveryId deliveryId,
    OrderId orderId,
    DeliveryPersonId deliveryPersonId,
    TenantId tenantId,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryAssignedEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryAssignedEvent of(DeliveryId deliveryId, OrderId orderId, 
                                          DeliveryPersonId deliveryPersonId, TenantId tenantId) {
        return new DeliveryAssignedEvent(deliveryId, orderId, deliveryPersonId, tenantId, Instant.now());
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return deliveryId.toString();
    }
    
    @Override
    public String getEventType() {
        return "DeliveryAssigned";
    }
}