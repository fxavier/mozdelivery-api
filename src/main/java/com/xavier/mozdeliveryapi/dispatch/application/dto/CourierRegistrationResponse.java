package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;

import java.time.Instant;

/**
 * Response for courier registration.
 */
public record CourierRegistrationResponse(
    UserId userId,
    DeliveryPersonId deliveryPersonId,
    String email,
    String fullName,
    String phoneNumber,
    CourierApprovalStatus approvalStatus,
    String city,
    String message,
    Instant registeredAt
) {
    
    /**
     * Create response for successful registration.
     */
    public static CourierRegistrationResponse success(
            UserId userId,
            DeliveryPersonId deliveryPersonId,
            String email,
            String fullName,
            String phoneNumber,
            String city,
            Instant registeredAt) {
        return new CourierRegistrationResponse(
            userId,
            deliveryPersonId,
            email,
            fullName,
            phoneNumber,
            CourierApprovalStatus.PENDING,
            city,
            "Registration submitted successfully. Your application is under review.",
            registeredAt
        );
    }
    
    /**
     * Create response for failed registration.
     */
    public static CourierRegistrationResponse failure(String message) {
        return new CourierRegistrationResponse(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            message,
            Instant.now()
        );
    }
}