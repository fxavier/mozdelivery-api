package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update delivery location.
 */
public record UpdateLocationRequest(
    @NotNull DeliveryId deliveryId,
    @NotNull Location newLocation
) {
}