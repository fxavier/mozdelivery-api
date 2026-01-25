package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.VehicleInfo;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update courier vehicle information.
 */
public record UpdateVehicleInfoRequest(
    @NotNull(message = "Delivery person ID is required")
    DeliveryPersonId deliveryPersonId,
    
    @NotNull(message = "Vehicle information is required")
    VehicleInfo vehicleInfo,
    
    @NotNull(message = "Delivery capacity is required")
    DeliveryCapacity deliveryCapacity
) {}