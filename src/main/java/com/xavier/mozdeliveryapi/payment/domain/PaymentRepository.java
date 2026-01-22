package com.xavier.mozdeliveryapi.payment.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.Repository;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Repository interface for Payment aggregate.
 */
public interface PaymentRepository extends Repository<Payment, PaymentId> {
    
    /**
     * Save a payment.
     */
    Payment save(Payment payment);
    
    /**
     * Find payment by ID.
     */
    Optional<Payment> findById(PaymentId paymentId);
    
    /**
     * Find payments by order ID.
     */
    List<Payment> findByOrderId(OrderId orderId);
    
    /**
     * Find payments by tenant ID.
     */
    List<Payment> findByTenantId(TenantId tenantId);
    
    /**
     * Find payment by gateway transaction ID.
     */
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    /**
     * Delete a payment.
     */
    void delete(PaymentId paymentId);
}