package com.xavier.mozdeliveryapi.dispatch.application.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Response containing delivery information.
 */
public record DeliveryResponse(
    DeliveryId deliveryId,
    TenantId tenantId,
    OrderId orderId,
    DeliveryPersonId deliveryPersonId,
    Route route,
    DeliveryStatus status,
    Location currentLocation,
    Instant estimatedArrival,
    List<DeliveryEvent> events,
    int orderWeight,
    int orderVolume,
    double progress,
    Duration timeToArrival,
    boolean isOverdue,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
            delivery.getDeliveryId(),
            delivery.getTenantId(),
            delivery.getOrderId(),
            delivery.getDeliveryPersonId(),
            delivery.getRoute(),
            delivery.getStatus(),
            delivery.getCurrentLocation(),
            delivery.getEstimatedArrival(),
            delivery.getEvents(),
            delivery.getOrderWeight(),
            delivery.getOrderVolume(),
            delivery.getProgress(),
            delivery.getTimeToArrival(),
            delivery.isOverdue(),
            delivery.getCreatedAt(),
            delivery.getUpdatedAt()
        );
    }
}