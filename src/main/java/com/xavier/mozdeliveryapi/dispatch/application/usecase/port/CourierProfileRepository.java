package com.xavier.mozdeliveryapi.dispatch.application.usecase.port;

import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CourierProfile aggregate.
 */
public interface CourierProfileRepository extends Repository<CourierProfile, DeliveryPersonId> {
    
    /**
     * Save a courier profile.
     */
    CourierProfile save(CourierProfile courierProfile);
    
    /**
     * Find courier profile by ID.
     */
    Optional<CourierProfile> findById(DeliveryPersonId deliveryPersonId);
    
    /**
     * Find courier profile by user ID.
     */
    Optional<CourierProfile> findByUserId(UserId userId);
    
    /**
     * Find courier profile by email.
     */
    Optional<CourierProfile> findByEmail(String email);
    
    /**
     * Find courier profile by phone number.
     */
    Optional<CourierProfile> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find couriers by approval status.
     */
    List<CourierProfile> findByApprovalStatus(CourierApprovalStatus approvalStatus);
    
    /**
     * Find couriers by delivery status.
     */
    List<CourierProfile> findByStatus(DeliveryPersonStatus status);
    
    /**
     * Find approved couriers by delivery status.
     */
    List<CourierProfile> findByApprovalStatusAndStatus(CourierApprovalStatus approvalStatus, 
                                                      DeliveryPersonStatus status);
    
    /**
     * Find couriers by city.
     */
    List<CourierProfile> findByCity(String city);
    
    /**
     * Find approved couriers by city.
     */
    List<CourierProfile> findByApprovalStatusAndCity(CourierApprovalStatus approvalStatus, String city);
    
    /**
     * Find available couriers in a city.
     */
    List<CourierProfile> findAvailableInCity(String city);
    
    /**
     * Count couriers by approval status.
     */
    long countByApprovalStatus(CourierApprovalStatus approvalStatus);
    
    /**
     * Count couriers by city.
     */
    long countByCity(String city);
    
    /**
     * Delete a courier profile (for testing purposes).
     */
    void delete(DeliveryPersonId deliveryPersonId);
}