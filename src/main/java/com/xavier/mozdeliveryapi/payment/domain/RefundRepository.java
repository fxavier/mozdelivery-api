package com.xavier.mozdeliveryapi.payment.domain;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.shared.domain.Repository;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Repository interface for Refund aggregate.
 */
public interface RefundRepository extends Repository<Refund, RefundId> {
    
    /**
     * Save a refund.
     */
    Refund save(Refund refund);
    
    /**
     * Find refund by ID.
     */
    Optional<Refund> findById(RefundId refundId);
    
    /**
     * Find refunds by payment ID.
     */
    List<Refund> findByPaymentId(PaymentId paymentId);
    
    /**
     * Find refunds by tenant ID.
     */
    List<Refund> findByTenantId(TenantId tenantId);
    
    /**
     * Find refund by gateway refund ID.
     */
    Optional<Refund> findByGatewayRefundId(String gatewayRefundId);
    
    /**
     * Delete a refund.
     */
    void delete(RefundId refundId);
}