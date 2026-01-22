package com.xavier.mozdeliveryapi.dispatch.application;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update delivery status.
 */
public record UpdateDeliveryStatusRequest(
    @NotNull DeliveryId deliveryId,
    @NotNull DeliveryStatus newStatus,
    String notes
) {
    // notes can be null
}