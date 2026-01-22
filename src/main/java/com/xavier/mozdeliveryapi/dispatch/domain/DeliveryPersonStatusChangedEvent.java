package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;

/**
 * Domain event fired when a delivery person's status changes.
 */
public record DeliveryPersonStatusChangedEvent(
    DeliveryPersonId deliveryPersonId,
    DeliveryPersonStatus oldStatus,
    DeliveryPersonStatus newStatus,
    Instant occurredOn
) implements DomainEvent {
    
    public DeliveryPersonStatusChangedEvent {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(oldStatus, "Old status cannot be null");
        Objects.requireNonNull(newStatus, "New status cannot be null");
        Objects.requireNonNull(occurredOn, "Occurred on cannot be null");
    }
    
    public static DeliveryPersonStatusChangedEvent of(DeliveryPersonId deliveryPersonId,
                                                     DeliveryPersonStatus oldStatus, 
                                                     DeliveryPersonStatus newStatus) {
        return new DeliveryPersonStatusChangedEvent(deliveryPersonId, oldStatus, newStatus, Instant.now());
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
        return "DeliveryPersonStatusChanged";
    }
}