package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.AvailabilitySchedule;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;

import jakarta.validation.constraints.NotNull;

/**
 * Request to update courier availability schedule.
 */
public record UpdateAvailabilityRequest(
    @NotNull(message = "Delivery person ID is required")
    DeliveryPersonId deliveryPersonId,
    
    @NotNull(message = "Availability schedule is required")
    AvailabilitySchedule availabilitySchedule
) {}