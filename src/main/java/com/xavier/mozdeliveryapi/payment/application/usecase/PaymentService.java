package com.xavier.mozdeliveryapi.payment.application.usecase;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentStatusResponse;

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
