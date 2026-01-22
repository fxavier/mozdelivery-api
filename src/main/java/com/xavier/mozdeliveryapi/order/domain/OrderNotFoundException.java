package com.xavier.mozdeliveryapi.order.domain;

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