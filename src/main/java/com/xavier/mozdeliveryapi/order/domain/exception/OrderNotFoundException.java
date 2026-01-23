package com.xavier.mozdeliveryapi.order.domain.exception;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * Exception thrown when an order is not found.
 */
public class OrderNotFoundException extends RuntimeException {
    
    private final OrderId orderId;
    
    public OrderNotFoundException(OrderId orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }
    
    public OrderId getOrderId() {
        return orderId;
    }
}