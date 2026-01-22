package com.xavier.mozdeliveryapi.payment.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.payment.domain.Payment;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.PaymentRepository;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * In-memory implementation of PaymentRepository for development.
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final Map<PaymentId, Payment> store = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        store.put(payment.getPaymentId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return Optional.ofNullable(store.get(paymentId));
    }

    @Override
    public List<Payment> findByOrderId(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        return store.values().stream()
                .filter(payment -> orderId.equals(payment.getOrderId()))
                .toList();
    }

    @Override
    public List<Payment> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(payment -> tenantId.equals(payment.getTenantId()))
                .toList();
    }

    @Override
    public Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId) {
        if (gatewayTransactionId == null) {
            return Optional.empty();
        }

        return store.values().stream()
                .filter(payment -> gatewayTransactionId.equals(payment.getGatewayTransactionId()))
                .findFirst();
    }

    @Override
    public void delete(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        store.remove(paymentId);
    }

    @Override
    public void delete(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        store.remove(payment.getPaymentId());
    }

    @Override
    public boolean existsById(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return store.containsKey(paymentId);
    }
}
