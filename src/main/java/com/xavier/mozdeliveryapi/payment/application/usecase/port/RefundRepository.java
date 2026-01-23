package com.xavier.mozdeliveryapi.payment.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

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