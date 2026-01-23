package com.xavier.mozdeliveryapi.payment.application.usecase;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.util.List;
import com.xavier.mozdeliveryapi.payment.application.dto.CreatePaymentRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.CreateRefundRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundResponse;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundStatusResponse;

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
