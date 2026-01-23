package com.xavier.mozdeliveryapi.payment.infra.persistence;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.RefundRepository;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

/**
 * In-memory implementation of RefundRepository for development.
 */
@Repository
public class RefundRepositoryImpl implements RefundRepository {

    private final Map<RefundId, Refund> store = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund refund) {
        Objects.requireNonNull(refund, "Refund cannot be null");
        store.put(refund.getRefundId(), refund);
        return refund;
    }

    @Override
    public Optional<Refund> findById(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        return Optional.ofNullable(store.get(refundId));
    }

    @Override
    public List<Refund> findByPaymentId(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return store.values().stream()
                .filter(refund -> paymentId.equals(refund.getPaymentId()))
                .toList();
    }

    @Override
    public List<Refund> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(refund -> tenantId.equals(refund.getTenantId()))
                .toList();
    }

    @Override
    public Optional<Refund> findByGatewayRefundId(String gatewayRefundId) {
        if (gatewayRefundId == null) {
            return Optional.empty();
        }

        return store.values().stream()
                .filter(refund -> gatewayRefundId.equals(refund.getGatewayRefundId()))
                .findFirst();
    }

    @Override
    public void delete(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        store.remove(refundId);
    }

    @Override
    public void delete(Refund refund) {
        Objects.requireNonNull(refund, "Refund cannot be null");
        store.remove(refund.getRefundId());
    }

    @Override
    public boolean existsById(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        return store.containsKey(refundId);
    }
}
