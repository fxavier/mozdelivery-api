package com.xavier.mozdeliveryapi.order.application;

import com.xavier.mozdeliveryapi.order.domain.*;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.util.List;

/**
 * Application service for order management operations.
 */
public interface OrderApplicationService {
    
    /**
     * Create a new order.
     */
    OrderResponse createOrder(CreateOrderRequest request);
    
    /**
     * Get an order by ID.
     */
    OrderResponse getOrder(OrderId orderId);
    
    /**
     * Get orders for a tenant with filtering.
     */
    List<OrderResponse> getOrdersForTenant(TenantId tenantId, OrderFilter filter);
    
    /**
     * Update order status.
     */
    OrderResponse updateOrderStatus(OrderId orderId, OrderStatus status);
    
    /**
     * Cancel an order.
     */
    OrderResponse cancelOrder(OrderId orderId, CancellationRequest request);
    
    /**
     * Process payment confirmation.
     */
    OrderResponse confirmPayment(OrderId orderId, PaymentConfirmationRequest request);
    
    /**
     * Process payment failure.
     */
    OrderResponse failPayment(OrderId orderId, PaymentFailureRequest request);
    
    /**
     * Get order statistics for a tenant.
     */
    OrderStatistics getOrderStatistics(TenantId tenantId);
}