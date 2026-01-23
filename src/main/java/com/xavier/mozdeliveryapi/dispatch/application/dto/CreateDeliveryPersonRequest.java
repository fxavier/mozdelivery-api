package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request to create a new delivery person.
 */
public record CreateDeliveryPersonRequest(
    @NotNull TenantId tenantId,
    @NotBlank String name,
    @NotBlank String phoneNumber,
    @NotBlank String vehicleType,
    @NotNull DeliveryCapacity capacity,
    @NotNull Location initialLocation
) {
    
    public CreateDeliveryPersonRequest {
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (phoneNumber != null && phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        if (vehicleType != null && vehicleType.isBlank()) {
            throw new IllegalArgumentException("Vehicle type cannot be blank");
        }
    }
}