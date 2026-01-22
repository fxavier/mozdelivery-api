package com.xavier.mozdeliveryapi.order.domain;

/**
 * Service for handling order refunds.
 */
public interface RefundService {
    
    /**
     * Process a refund for a cancelled order.
     */
    RefundResult processRefund(Order order, RefundReason reason);
    
    /**
     * Check if an order is eligible for refund.
     */
    boolean isEligibleForRefund(Order order);
    
    /**
     * Calculate refund amount for an order.
     */
    Money calculateRefundAmount(Order order);
    
    /**
     * Get refund status for an order.
     */
    RefundStatus getRefundStatus(OrderId orderId);
}