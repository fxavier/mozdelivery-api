package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;

/**
 * Domain service for dispatch operations.
 */
public interface DispatchService {
    
    /**
     * Assign a delivery for an order.
     * 
     * @param tenantId the tenant ID
     * @param orderId the order ID
     * @param pickupLocation the pickup location
     * @param deliveryLocation the delivery location
     * @param orderWeight the order weight in grams
     * @param orderVolume the order volume in cubic centimeters
     * @return the created delivery
     * @throws DeliveryAssignmentException if no suitable delivery person is available
     */
    Delivery assignDelivery(TenantId tenantId, OrderId orderId, Location pickupLocation, 
                           Location deliveryLocation, int orderWeight, int orderVolume);
    
    /**
     * Reassign a delivery to a different delivery person.
     * 
     * @param deliveryId the delivery ID
     * @param newDeliveryPersonId the new delivery person ID
     * @return the updated delivery
     * @throws DeliveryNotFoundException if delivery is not found
     * @throws DeliveryAssignmentException if the new delivery person cannot handle the order
     */
    Delivery reassignDelivery(DeliveryId deliveryId, DeliveryPersonId newDeliveryPersonId);
    
    /**
     * Update delivery status.
     * 
     * @param deliveryId the delivery ID
     * @param newStatus the new status
     * @param notes optional notes
     * @return the updated delivery
     * @throws DeliveryNotFoundException if delivery is not found
     */
    Delivery updateDeliveryStatus(DeliveryId deliveryId, DeliveryStatus newStatus, String notes);
    
    /**
     * Update delivery status without notes.
     * 
     * @param deliveryId the delivery ID
     * @param newStatus the new status
     * @return the updated delivery
     * @throws DeliveryNotFoundException if delivery is not found
     */
    Delivery updateDeliveryStatus(DeliveryId deliveryId, DeliveryStatus newStatus);
    
    /**
     * Update delivery location.
     * 
     * @param deliveryId the delivery ID
     * @param newLocation the new location
     * @return the updated delivery
     * @throws DeliveryNotFoundException if delivery is not found
     */
    Delivery updateDeliveryLocation(DeliveryId deliveryId, Location newLocation);
    
    /**
     * Cancel a delivery.
     * 
     * @param deliveryId the delivery ID
     * @param reason the cancellation reason
     * @return the updated delivery
     * @throws DeliveryNotFoundException if delivery is not found
     */
    Delivery cancelDelivery(DeliveryId deliveryId, String reason);
    
    /**
     * Get all active deliveries for a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @return list of active deliveries
     */
    List<Delivery> getActiveDeliveriesForPerson(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get all overdue deliveries.
     * 
     * @return list of overdue deliveries
     */
    List<Delivery> getOverdueDeliveries();
    
    /**
     * Optimize route for multiple deliveries assigned to a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param deliveryIds list of delivery IDs to optimize
     * @return optimized route
     */
    Route optimizeRoute(DeliveryPersonId deliveryPersonId, List<DeliveryId> deliveryIds);
}