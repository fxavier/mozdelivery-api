package com.xavier.mozdeliveryapi.dispatch.application.usecase.port;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;

/**
 * Repository interface for Delivery aggregate.
 */
public interface DeliveryRepository extends Repository<Delivery, DeliveryId> {
    
    /**
     * Save a delivery.
     */
    Delivery save(Delivery delivery);
    
    /**
     * Find delivery by ID.
     */
    Optional<Delivery> findById(DeliveryId deliveryId);
    
    /**
     * Find delivery by order ID.
     */
    Optional<Delivery> findByOrderId(OrderId orderId);
    
    /**
     * Find all deliveries for a tenant.
     */
    List<Delivery> findByTenantId(TenantId tenantId);
    
    /**
     * Find all deliveries assigned to a delivery person.
     */
    List<Delivery> findByDeliveryPersonId(DeliveryPersonId deliveryPersonId);
    
    /**
     * Find all active deliveries for a delivery person.
     */
    List<Delivery> findActiveByDeliveryPersonId(DeliveryPersonId deliveryPersonId);
    
    /**
     * Find all deliveries with a specific status.
     */
    List<Delivery> findByStatus(DeliveryStatus status);
    
    /**
     * Find all deliveries with status in the given list.
     */
    List<Delivery> findByStatusIn(List<DeliveryStatus> statuses);
    
    /**
     * Find overdue deliveries (estimated arrival time has passed).
     */
    List<Delivery> findOverdueDeliveries(Instant currentTime);
    
    /**
     * Find deliveries created within a time range.
     */
    List<Delivery> findByCreatedAtBetween(Instant startTime, Instant endTime);
    
    /**
     * Count active deliveries for a delivery person.
     */
    long countActiveByDeliveryPersonId(DeliveryPersonId deliveryPersonId);
    
    /**
     * Delete a delivery (for testing purposes).
     */
    void delete(DeliveryId deliveryId);
}