package com.xavier.mozdeliveryapi.dispatch.application;

import java.time.Instant;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPerson;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Response containing delivery person information.
 */
public record DeliveryPersonResponse(
    DeliveryPersonId deliveryPersonId,
    TenantId tenantId,
    String name,
    String phoneNumber,
    String vehicleType,
    DeliveryCapacity capacity,
    DeliveryPersonStatus status,
    Location currentLocation,
    int currentOrderCount,
    int currentWeight,
    int currentVolume,
    double capacityUtilization,
    DeliveryCapacity remainingCapacity,
    boolean isAvailable,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static DeliveryPersonResponse from(DeliveryPerson deliveryPerson) {
        return new DeliveryPersonResponse(
            deliveryPerson.getDeliveryPersonId(),
            deliveryPerson.getTenantId(),
            deliveryPerson.getName(),
            deliveryPerson.getPhoneNumber(),
            deliveryPerson.getVehicleType(),
            deliveryPerson.getCapacity(),
            deliveryPerson.getStatus(),
            deliveryPerson.getCurrentLocation(),
            deliveryPerson.getCurrentOrderCount(),
            deliveryPerson.getCurrentWeight(),
            deliveryPerson.getCurrentVolume(),
            deliveryPerson.getCapacityUtilization(),
            deliveryPerson.getRemainingCapacity(),
            deliveryPerson.isAvailable(),
            deliveryPerson.getCreatedAt(),
            deliveryPerson.getUpdatedAt()
        );
    }
}