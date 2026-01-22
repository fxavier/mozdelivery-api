package com.xavier.mozdeliveryapi.dispatch.application;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryId;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update delivery location.
 */
public record UpdateLocationRequest(
    @NotNull DeliveryId deliveryId,
    @NotNull Location newLocation
) {
}