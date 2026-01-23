package com.xavier.mozdeliveryapi.dispatch.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.DeliveryPerson;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;

/**
 * Repository interface for DeliveryPerson aggregate.
 */
public interface DeliveryPersonRepository extends Repository<DeliveryPerson, DeliveryPersonId> {
    
    /**
     * Save a delivery person.
     */
    DeliveryPerson save(DeliveryPerson deliveryPerson);
    
    /**
     * Find delivery person by ID.
     */
    Optional<DeliveryPerson> findById(DeliveryPersonId deliveryPersonId);
    
    /**
     * Find all delivery persons for a tenant.
     */
    List<DeliveryPerson> findByTenantId(TenantId tenantId);
    
    /**
     * Find all available delivery persons for a tenant.
     */
    List<DeliveryPerson> findAvailableByTenantId(TenantId tenantId);
    
    /**
     * Find delivery persons by status.
     */
    List<DeliveryPerson> findByStatus(DeliveryPersonStatus status);
    
    /**
     * Find delivery persons by status for a specific tenant.
     */
    List<DeliveryPerson> findByTenantIdAndStatus(TenantId tenantId, DeliveryPersonStatus status);
    
    /**
     * Find available delivery persons within a certain distance of a location.
     */
    List<DeliveryPerson> findAvailableWithinDistance(TenantId tenantId, Location location, Distance maxDistance);
    
    /**
     * Find delivery persons by vehicle type.
     */
    List<DeliveryPerson> findByVehicleType(String vehicleType);
    
    /**
     * Find delivery persons by vehicle type for a specific tenant.
     */
    List<DeliveryPerson> findByTenantIdAndVehicleType(TenantId tenantId, String vehicleType);
    
    /**
     * Find delivery person by phone number (for uniqueness check).
     */
    Optional<DeliveryPerson> findByPhoneNumber(String phoneNumber);
    
    /**
     * Count total delivery persons for a tenant.
     */
    long countByTenantId(TenantId tenantId);
    
    /**
     * Count available delivery persons for a tenant.
     */
    long countAvailableByTenantId(TenantId tenantId);
    
    /**
     * Delete a delivery person (for testing purposes).
     */
    void delete(DeliveryPersonId deliveryPersonId);
}