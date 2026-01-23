package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * Request to assign a delivery for an order.
 */
public record AssignDeliveryRequest(
    @NotNull TenantId tenantId,
    @NotNull OrderId orderId,
    @NotNull Location pickupLocation,
    @NotNull Location deliveryLocation,
    @Min(0) int orderWeight,
    @Min(0) int orderVolume
) {
    
    public AssignDeliveryRequest {
        if (orderWeight < 0) {
            throw new IllegalArgumentException("Order weight cannot be negative");
        }
        if (orderVolume < 0) {
            throw new IllegalArgumentException("Order volume cannot be negative");
        }
    }
}