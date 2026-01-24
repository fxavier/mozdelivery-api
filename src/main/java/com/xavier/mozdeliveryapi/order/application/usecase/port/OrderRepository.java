package com.xavier.mozdeliveryapi.order.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

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
     * Find all orders for a specific merchant.
     */
    List<Order> findByMerchantId(MerchantId merchantId);
    
    /**
     * Find orders by merchant and status.
     */
    List<Order> findByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status);
    
    /**
     * Find orders by customer.
     */
    List<Order> findByCustomerId(CustomerId customerId);
    
    /**
     * Find orders by customer and merchant.
     */
    List<Order> findByCustomerIdAndMerchantId(CustomerId customerId, MerchantId merchantId);
    
    /**
     * Find orders by status.
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Find guest order by tracking token.
     */
    Optional<Order> findByGuestTrackingToken(GuestTrackingToken token);
    
    /**
     * Find all guest orders for a merchant.
     */
    List<Order> findGuestOrdersByMerchantId(MerchantId merchantId);
    
    /**
     * Find guest orders by merchant and status.
     */
    List<Order> findGuestOrdersByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status);
    
    /**
     * Check if an order exists.
     */
    boolean existsById(OrderId orderId);
    
    /**
     * Delete an order (should be used carefully).
     */
    void deleteById(OrderId orderId);
    
    /**
     * Count orders by merchant.
     */
    long countByMerchantId(MerchantId merchantId);
    
    /**
     * Count orders by merchant and status.
     */
    long countByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status);
    
    /**
     * Count guest orders by merchant.
     */
    long countGuestOrdersByMerchantId(MerchantId merchantId);
}