package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.util.List;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CreateOrderCommand;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;

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