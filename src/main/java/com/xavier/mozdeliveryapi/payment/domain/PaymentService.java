package com.xavier.mozdeliveryapi.payment.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain service for payment operations.
 */
public interface PaymentService {
    
    /**
     * Create a new payment.
     */
    Payment createPayment(PaymentRequest request);
    
    /**
     * Process a payment through the appropriate gateway.
     */
    PaymentResult processPayment(PaymentId paymentId);
    
    /**
     * Check payment status with the gateway.
     */
    PaymentStatusResponse checkPaymentStatus(PaymentId paymentId);
    
    /**
     * Cancel a payment.
     */
    void cancelPayment(PaymentId paymentId);
    
    /**
     * Get payment by ID.
     */
    Optional<Payment> getPayment(PaymentId paymentId);
    
    /**
     * Get payments for an order.
     */
    List<Payment> getPaymentsForOrder(OrderId orderId);
    
    /**
     * Get payments for a tenant.
     */
    List<Payment> getPaymentsForTenant(TenantId tenantId);
}