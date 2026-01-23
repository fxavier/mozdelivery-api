package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import java.util.List;
import com.xavier.mozdeliveryapi.order.application.dto.CancellationRequest;
import com.xavier.mozdeliveryapi.order.application.dto.CreateOrderRequest;
import com.xavier.mozdeliveryapi.order.application.dto.OrderResponse;
import com.xavier.mozdeliveryapi.order.application.dto.OrderStatistics;
import com.xavier.mozdeliveryapi.order.application.dto.PaymentConfirmationRequest;
import com.xavier.mozdeliveryapi.order.application.dto.PaymentFailureRequest;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;

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