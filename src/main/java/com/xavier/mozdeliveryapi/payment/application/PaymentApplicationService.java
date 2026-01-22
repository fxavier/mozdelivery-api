package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.RefundReason;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.RefundId;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.util.List;

/**
 * Application service for payment operations.
 */
public interface PaymentApplicationService {
    
    /**
     * Create a new payment.
     */
    PaymentResponse createPayment(CreatePaymentRequest request);
    
    /**
     * Process a payment.
     */
    PaymentResponse processPayment(PaymentId paymentId);
    
    /**
     * Get payment by ID.
     */
    PaymentResponse getPayment(PaymentId paymentId);
    
    /**
     * Get payments for an order.
     */
    List<PaymentResponse> getPaymentsForOrder(OrderId orderId);
    
    /**
     * Get payments for a tenant.
     */
    List<PaymentResponse> getPaymentsForTenant(TenantId tenantId);
    
    /**
     * Cancel a payment.
     */
    void cancelPayment(PaymentId paymentId);
    
    /**
     * Check payment status.
     */
    PaymentStatusResponse checkPaymentStatus(PaymentId paymentId);
    
    /**
     * Create a refund.
     */
    RefundResponse createRefund(CreateRefundRequest request);
    
    /**
     * Process a refund.
     */
    RefundResponse processRefund(RefundId refundId);
    
    /**
     * Get refund by ID.
     */
    RefundResponse getRefund(RefundId refundId);
    
    /**
     * Get refunds for a payment.
     */
    List<RefundResponse> getRefundsForPayment(PaymentId paymentId);
    
    /**
     * Get refunds for a tenant.
     */
    List<RefundResponse> getRefundsForTenant(TenantId tenantId);
    
    /**
     * Cancel a refund.
     */
    void cancelRefund(RefundId refundId);
    
    /**
     * Check refund status.
     */
    RefundStatusResponse checkRefundStatus(RefundId refundId);
    
    /**
     * Calculate maximum refundable amount.
     */
    Money calculateMaxRefundableAmount(PaymentId paymentId);
}