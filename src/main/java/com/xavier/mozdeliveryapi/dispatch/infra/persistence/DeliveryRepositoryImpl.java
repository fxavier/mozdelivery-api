package com.xavier.mozdeliveryapi.dispatch.infra.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.DeliveryRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * In-memory implementation of DeliveryRepository for development.
 */
@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final Map<DeliveryId, Delivery> store = new ConcurrentHashMap<>();

    @Override
    public Delivery save(Delivery delivery) {
        Objects.requireNonNull(delivery, "Delivery cannot be null");
        store.put(delivery.getDeliveryId(), delivery);
        return delivery;
    }

    @Override
    public Optional<Delivery> findById(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        return Optional.ofNullable(store.get(deliveryId));
    }

    @Override
    public Optional<Delivery> findByOrderId(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        return store.values().stream()
                .filter(delivery -> orderId.equals(delivery.getOrderId()))
                .findFirst();
    }

    @Override
    public List<Delivery> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(delivery -> tenantId.equals(delivery.getTenantId()))
                .toList();
    }

    @Override
    public List<Delivery> findByDeliveryPersonId(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return store.values().stream()
                .filter(delivery -> deliveryPersonId.equals(delivery.getDeliveryPersonId()))
                .toList();
    }

    @Override
    public List<Delivery> findActiveByDeliveryPersonId(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return store.values().stream()
                .filter(delivery -> deliveryPersonId.equals(delivery.getDeliveryPersonId()))
                .filter(delivery -> delivery.getStatus().isActive())
                .toList();
    }

    @Override
    public List<Delivery> findByStatus(DeliveryStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        return store.values().stream()
                .filter(delivery -> status == delivery.getStatus())
                .toList();
    }

    @Override
    public List<Delivery> findByStatusIn(List<DeliveryStatus> statuses) {
        Objects.requireNonNull(statuses, "Statuses cannot be null");
        return store.values().stream()
                .filter(delivery -> statuses.contains(delivery.getStatus()))
                .toList();
    }

    @Override
    public List<Delivery> findOverdueDeliveries(Instant currentTime) {
        Objects.requireNonNull(currentTime, "Current time cannot be null");
        return store.values().stream()
                .filter(delivery -> delivery.getEstimatedArrival() != null)
                .filter(delivery -> delivery.getEstimatedArrival().isBefore(currentTime))
                .filter(delivery -> !delivery.getStatus().isCompleted())
                .toList();
    }

    @Override
    public List<Delivery> findByCreatedAtBetween(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        return store.values().stream()
                .filter(delivery -> !delivery.getCreatedAt().isBefore(startTime))
                .filter(delivery -> !delivery.getCreatedAt().isAfter(endTime))
                .toList();
    }

    @Override
    public long countActiveByDeliveryPersonId(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return store.values().stream()
                .filter(delivery -> deliveryPersonId.equals(delivery.getDeliveryPersonId()))
                .filter(delivery -> delivery.getStatus().isActive())
                .count();
    }

    @Override
    public void delete(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        store.remove(deliveryId);
    }

    @Override
    public void delete(Delivery delivery) {
        Objects.requireNonNull(delivery, "Delivery cannot be null");
        store.remove(delivery.getDeliveryId());
    }

    @Override
    public boolean existsById(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        return store.containsKey(deliveryId);
    }
}
