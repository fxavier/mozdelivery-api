package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryAssignment;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;

/**
 * Domain service for delivery assignment logic.
 */
public interface DeliveryAssignmentService {
    
    /**
     * Find the best delivery person for an order based on location, capacity, and other factors.
     * 
     * @param tenantId the tenant ID
     * @param orderId the order ID
     * @param pickupLocation the pickup location
     * @param deliveryLocation the delivery location
     * @param orderWeight the order weight in grams
     * @param orderVolume the order volume in cubic centimeters
     * @return the best delivery assignment, or empty if no suitable delivery person is available
     */
    Optional<DeliveryAssignment> findBestDeliveryPerson(TenantId tenantId, OrderId orderId,
                                                       Location pickupLocation, Location deliveryLocation,
                                                       int orderWeight, int orderVolume);
    
    /**
     * Get all possible delivery assignments for an order, ranked by suitability.
     * 
     * @param tenantId the tenant ID
     * @param orderId the order ID
     * @param pickupLocation the pickup location
     * @param deliveryLocation the delivery location
     * @param orderWeight the order weight in grams
     * @param orderVolume the order volume in cubic centimeters
     * @return list of delivery assignments ranked by score (best first)
     */
    List<DeliveryAssignment> getAllPossibleAssignments(TenantId tenantId, OrderId orderId,
                                                       Location pickupLocation, Location deliveryLocation,
                                                       int orderWeight, int orderVolume);
    
    /**
     * Check if a delivery person can handle an order.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param orderWeight the order weight in grams
     * @param orderVolume the order volume in cubic centimeters
     * @return true if the delivery person can handle the order
     */
    boolean canDeliveryPersonHandleOrder(DeliveryPersonId deliveryPersonId, int orderWeight, int orderVolume);
    
    /**
     * Calculate the priority score for an order assignment.
     * 
     * @param orderId the order ID
     * @param pickupLocation the pickup location
     * @param deliveryLocation the delivery location
     * @return priority score (higher means higher priority)
     */
    int calculateOrderPriority(OrderId orderId, Location pickupLocation, Location deliveryLocation);
}