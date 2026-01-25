package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierApprovalRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateAvailabilityRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateVehicleInfoRequest;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;

import java.util.List;

/**
 * Service for courier registration and management operations.
 */
public interface CourierRegistrationService {
    
    /**
     * Register a new courier.
     */
    CourierRegistrationResponse registerCourier(CourierRegistrationRequest request);
    
    /**
     * Approve or reject a courier registration.
     */
    CourierProfile processApproval(CourierApprovalRequest request);
    
    /**
     * Update courier vehicle information.
     */
    CourierProfile updateVehicleInfo(UpdateVehicleInfoRequest request);
    
    /**
     * Update courier availability schedule.
     */
    CourierProfile updateAvailability(UpdateAvailabilityRequest request);
    
    /**
     * Get courier profile by ID.
     */
    CourierProfile getCourierProfile(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get all pending courier registrations.
     */
    List<CourierProfile> getPendingRegistrations();
    
    /**
     * Get couriers by approval status.
     */
    List<CourierProfile> getCouriersByStatus(CourierApprovalStatus status);
    
    /**
     * Get couriers by city.
     */
    List<CourierProfile> getCouriersByCity(String city);
    
    /**
     * Suspend courier account.
     */
    CourierProfile suspendCourier(DeliveryPersonId deliveryPersonId, String reason);
    
    /**
     * Reactivate suspended courier.
     */
    CourierProfile reactivateCourier(DeliveryPersonId deliveryPersonId);
}