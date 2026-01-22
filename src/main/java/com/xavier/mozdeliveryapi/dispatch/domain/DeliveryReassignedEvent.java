package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a delivery is reassigned to a different delivery person.
 */
public record DeliveryReassignedEvent(
    DeliveryId deliveryId,
    OrderId orderId,
    DeliveryPersonId oldDeliveryPersonId,
    DeliveryPersonId newDeliveryPersonId,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryReassignedEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(oldDeliveryPersonId, "Old delivery person ID cannot be null");
        Objects.requireNonNull(newDeliveryPersonId, "New delivery person ID cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryReassignedEvent of(DeliveryId deliveryId, OrderId orderId,
                                            DeliveryPersonId oldDeliveryPersonId, 
                                            DeliveryPersonId newDeliveryPersonId) {
        return new DeliveryReassignedEvent(deliveryId, orderId, oldDeliveryPersonId, 
                                          newDeliveryPersonId, Instant.now());
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
        return "DeliveryReassigned";
    }
}