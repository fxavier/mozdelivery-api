package com.xavier.mozdeliveryapi.dispatch.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import java.util.Objects;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;


/**
 * Domain event fired when a delivery is completed successfully.
 */
public record DeliveryCompletedEvent(
    DeliveryId deliveryId,
    OrderId orderId,
    DeliveryPersonId deliveryPersonId,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryCompletedEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryCompletedEvent of(DeliveryId deliveryId, OrderId orderId, 
                                           DeliveryPersonId deliveryPersonId) {
        return new DeliveryCompletedEvent(deliveryId, orderId, deliveryPersonId, Instant.now());
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
        return "DeliveryCompleted";
    }
}