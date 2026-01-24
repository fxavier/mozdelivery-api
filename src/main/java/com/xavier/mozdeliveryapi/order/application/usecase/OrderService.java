package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CreateOrderCommand;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

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
     * Find orders for a merchant with optional filtering.
     */
    List<Order> findOrdersByMerchant(MerchantId merchantId, OrderFilter filter);
    
    /**
     * Validate if an order can be created for the given merchant.
     */
    void validateOrderCreation(CreateOrderCommand command);
    
    /**
     * Calculate delivery fee for an order.
     */
    Money calculateDeliveryFee(MerchantId merchantId, DeliveryAddress address, List<OrderItem> items);
}