package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;

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