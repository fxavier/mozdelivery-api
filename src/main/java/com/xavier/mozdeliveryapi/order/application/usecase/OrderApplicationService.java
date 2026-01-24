package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.order.application.dto.CancellationRequest;
import com.xavier.mozdeliveryapi.order.application.dto.CreateOrderRequest;
import com.xavier.mozdeliveryapi.order.application.dto.OrderResponse;
import com.xavier.mozdeliveryapi.order.application.dto.OrderStatistics;
import com.xavier.mozdeliveryapi.order.application.dto.PaymentConfirmationRequest;
import com.xavier.mozdeliveryapi.order.application.dto.PaymentFailureRequest;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

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
     * Get orders for a merchant with filtering.
     */
    List<OrderResponse> getOrdersForMerchant(MerchantId merchantId, OrderFilter filter);
    
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
     * Get order statistics for a merchant.
     */
    OrderStatistics getOrderStatistics(MerchantId merchantId);
}