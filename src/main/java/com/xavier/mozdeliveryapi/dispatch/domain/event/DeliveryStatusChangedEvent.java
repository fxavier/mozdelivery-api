package com.xavier.mozdeliveryapi.dispatch.domain.event;

import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.time.Instant;
import java.util.Objects;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;


/**
 * Domain event fired when a delivery status changes.
 */
public record DeliveryStatusChangedEvent(
    DeliveryId deliveryId,
    OrderId orderId,
    DeliveryStatus oldStatus,
    DeliveryStatus newStatus,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryStatusChangedEvent {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(oldStatus, "Old status cannot be null");
        Objects.requireNonNull(newStatus, "New status cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryStatusChangedEvent of(DeliveryId deliveryId, OrderId orderId,
                                               DeliveryStatus oldStatus, DeliveryStatus newStatus) {
        return new DeliveryStatusChangedEvent(deliveryId, orderId, oldStatus, newStatus, Instant.now());
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
        return "DeliveryStatusChanged";
    }
}