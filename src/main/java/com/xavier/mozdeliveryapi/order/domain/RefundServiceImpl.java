package com.xavier.mozdeliveryapi.order.domain;

import java.util.Objects;

import org.springframework.stereotype.Service;

/**
 * Implementation of RefundService.
 */
@Service("orderRefundService")
public class RefundServiceImpl implements RefundService {
    
    @Override
    public RefundResult processRefund(Order order, RefundReason reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(reason, "Refund reason cannot be null");
        
        if (!isEligibleForRefund(order)) {
            throw new IllegalStateException("Order is not eligible for refund: " + order.getOrderId());
        }
        
        Money refundAmount = calculateRefundAmount(order);
        RefundId refundId = RefundId.generate();
        
        // For now, simulate successful refund
        // In real implementation, this would integrate with payment gateways
        if (order.getPaymentInfo().method().supportsRefunds()) {
            return RefundResult.successful(refundId, order.getOrderId(), refundAmount, "gateway-txn-" + refundId);
        } else {
            return RefundResult.failed(refundId, order.getOrderId(), refundAmount, 
                "Payment method does not support refunds");
        }
    }
    
    @Override
    public boolean isEligibleForRefund(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // Order must be cancelled or delivered to be eligible for refund
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.DELIVERED) {
            return false;
        }
        
        // Payment method must support refunds
        if (!order.getPaymentInfo().method().supportsRefunds()) {
            return false;
        }
        
        // Payment must have been completed
        return order.getPaymentInfo().isPaid();
    }
    
    @Override
    public Money calculateRefundAmount(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // For now, refund the full amount
        // In real implementation, this could consider partial refunds, fees, etc.
        return order.getTotalAmount();
    }
    
    @Override
    public RefundStatus getRefundStatus(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // For now, return pending status
        // In real implementation, this would query the refund repository
        return RefundStatus.PENDING;
    }
}
