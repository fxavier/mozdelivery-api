package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.util.List;

/**
 * Domain service for order-related business operations.
 */
public interface OrderService {
    
    /**
     * Create a new order with validation.
     */
    Order createOrder(CreateOrderCommand command);
    
    /**
     * Update order status with business rule validation.
     */
    Order updateOrderStatus(OrderId orderId, OrderStatus status);
    
    /**
     * Cancel an order with validation.
     */
    Order cancelOrder(OrderId orderId, CancellationReason reason, String details);
    
    /**
     * Cancel an order with validation (no details).
     */
    Order cancelOrder(OrderId orderId, CancellationReason reason);
    
    /**
     * Find orders for a tenant with optional filtering.
     */
    List<Order> findOrdersByTenant(TenantId tenantId, OrderFilter filter);
    
    /**
     * Validate if an order can be created for the given tenant.
     */
    void validateOrderCreation(CreateOrderCommand command);
    
    /**
     * Calculate delivery fee for an order.
     */
    Money calculateDeliveryFee(TenantId tenantId, DeliveryAddress address, List<OrderItem> items);
}