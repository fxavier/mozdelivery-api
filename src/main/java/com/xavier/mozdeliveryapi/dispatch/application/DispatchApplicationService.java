package com.xavier.mozdeliveryapi.dispatch.application;

import java.util.List;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Application service for dispatch operations.
 */
public interface DispatchApplicationService {
    
    /**
     * Create a new delivery person.
     */
    DeliveryPersonResponse createDeliveryPerson(CreateDeliveryPersonRequest request);
    
    /**
     * Assign a delivery for an order.
     */
    DeliveryResponse assignDelivery(AssignDeliveryRequest request);
    
    /**
     * Update delivery status.
     */
    DeliveryResponse updateDeliveryStatus(UpdateDeliveryStatusRequest request);
    
    /**
     * Update delivery location.
     */
    DeliveryResponse updateDeliveryLocation(UpdateLocationRequest request);
    
    /**
     * Cancel a delivery.
     */
    DeliveryResponse cancelDelivery(DeliveryId deliveryId, String reason);
    
    /**
     * Reassign a delivery to a different delivery person.
     */
    DeliveryResponse reassignDelivery(DeliveryId deliveryId, DeliveryPersonId newDeliveryPersonId);
    
    /**
     * Get delivery by ID.
     */
    DeliveryResponse getDelivery(DeliveryId deliveryId);
    
    /**
     * Get delivery person by ID.
     */
    DeliveryPersonResponse getDeliveryPerson(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get all delivery persons for a tenant.
     */
    List<DeliveryPersonResponse> getDeliveryPersonsForTenant(TenantId tenantId);
    
    /**
     * Get available delivery persons for a tenant.
     */
    List<DeliveryPersonResponse> getAvailableDeliveryPersonsForTenant(TenantId tenantId);
    
    /**
     * Get all deliveries for a tenant.
     */
    List<DeliveryResponse> getDeliveriesForTenant(TenantId tenantId);
    
    /**
     * Get active deliveries for a delivery person.
     */
    List<DeliveryResponse> getActiveDeliveriesForPerson(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get overdue deliveries.
     */
    List<DeliveryResponse> getOverdueDeliveries();
    
    /**
     * Update delivery person status.
     */
    DeliveryPersonResponse updateDeliveryPersonStatus(DeliveryPersonId deliveryPersonId, 
                                                     DeliveryPersonStatus newStatus);
    
    /**
     * Update delivery person location.
     */
    DeliveryPersonResponse updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, 
                                                       Location newLocation);
}