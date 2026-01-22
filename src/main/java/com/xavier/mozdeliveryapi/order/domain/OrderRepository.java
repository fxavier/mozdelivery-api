package com.xavier.mozdeliveryapi.order.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.shared.domain.Repository;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Repository interface for Order aggregate.
 */
public interface OrderRepository extends Repository<Order, OrderId> {
    
    /**
     * Save an order.
     */
    Order save(Order order);
    
    /**
     * Find an order by its ID.
     */
    Optional<Order> findById(OrderId orderId);
    
    /**
     * Find all orders for a specific tenant.
     */
    List<Order> findByTenantId(TenantId tenantId);
    
    /**
     * Find orders by tenant and status.
     */
    List<Order> findByTenantIdAndStatus(TenantId tenantId, OrderStatus status);
    
    /**
     * Find orders by customer.
     */
    List<Order> findByCustomerId(CustomerId customerId);
    
    /**
     * Find orders by customer and tenant.
     */
    List<Order> findByCustomerIdAndTenantId(CustomerId customerId, TenantId tenantId);
    
    /**
     * Find orders by status.
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Check if an order exists.
     */
    boolean existsById(OrderId orderId);
    
    /**
     * Delete an order (should be used carefully).
     */
    void deleteById(OrderId orderId);
    
    /**
     * Count orders by tenant.
     */
    long countByTenantId(TenantId tenantId);
    
    /**
     * Count orders by tenant and status.
     */
    long countByTenantIdAndStatus(TenantId tenantId, OrderStatus status);
}