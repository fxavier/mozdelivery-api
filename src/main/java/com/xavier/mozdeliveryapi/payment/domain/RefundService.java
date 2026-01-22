package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.RefundReason;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for refund operations.
 */
public interface RefundService {
    
    /**
     * Create a new refund for a payment.
     */
    Refund createRefund(PaymentId paymentId, Money amount, RefundReason reason, String description);
    
    /**
     * Process a refund through the appropriate gateway.
     */
    RefundResult processRefund(RefundId refundId);
    
    /**
     * Check refund status with the gateway.
     */
    RefundStatusResponse checkRefundStatus(RefundId refundId);
    
    /**
     * Cancel a refund.
     */
    void cancelRefund(RefundId refundId);
    
    /**
     * Get refund by ID.
     */
    Optional<Refund> getRefund(RefundId refundId);
    
    /**
     * Get refunds for a payment.
     */
    List<Refund> getRefundsForPayment(PaymentId paymentId);
    
    /**
     * Get refunds for a tenant.
     */
    List<Refund> getRefundsForTenant(TenantId tenantId);
    
    /**
     * Calculate maximum refundable amount for a payment.
     */
    Money calculateMaxRefundableAmount(PaymentId paymentId);
    
    /**
     * Check if a payment can be refunded.
     */
    boolean canRefundPayment(PaymentId paymentId, Money amount);
}