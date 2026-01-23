package com.xavier.mozdeliveryapi.order.application.usecase;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.RefundResult;

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
