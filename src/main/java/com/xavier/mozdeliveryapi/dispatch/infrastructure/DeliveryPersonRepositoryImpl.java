package com.xavier.mozdeliveryapi.dispatch.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPerson;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.geospatial.domain.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * In-memory implementation of DeliveryPersonRepository for development.
 */
@Repository
public class DeliveryPersonRepositoryImpl implements DeliveryPersonRepository {

    private final Map<DeliveryPersonId, DeliveryPerson> store = new ConcurrentHashMap<>();

    @Override
    public DeliveryPerson save(DeliveryPerson deliveryPerson) {
        Objects.requireNonNull(deliveryPerson, "Delivery person cannot be null");
        store.put(deliveryPerson.getDeliveryPersonId(), deliveryPerson);
        return deliveryPerson;
    }

    @Override
    public Optional<DeliveryPerson> findById(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return Optional.ofNullable(store.get(deliveryPersonId));
    }

    @Override
    public List<DeliveryPerson> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .toList();
    }

    @Override
    public List<DeliveryPerson> findAvailableByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .filter(person -> person.getStatus().isAvailable())
                .toList();
    }

    @Override
    public List<DeliveryPerson> findByStatus(DeliveryPersonStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        return store.values().stream()
                .filter(person -> status == person.getStatus())
                .toList();
    }

    @Override
    public List<DeliveryPerson> findByTenantIdAndStatus(TenantId tenantId, DeliveryPersonStatus status) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .filter(person -> status == person.getStatus())
                .toList();
    }

    @Override
    public List<DeliveryPerson> findAvailableWithinDistance(TenantId tenantId, Location location, Distance maxDistance) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(maxDistance, "Max distance cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .filter(person -> person.getStatus().isAvailable())
                .filter(person -> person.getCurrentLocation().distanceTo(location).compareTo(maxDistance) <= 0)
                .toList();
    }

    @Override
    public List<DeliveryPerson> findByVehicleType(String vehicleType) {
        Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        return store.values().stream()
                .filter(person -> vehicleType.equalsIgnoreCase(person.getVehicleType()))
                .toList();
    }

    @Override
    public List<DeliveryPerson> findByTenantIdAndVehicleType(TenantId tenantId, String vehicleType) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .filter(person -> vehicleType.equalsIgnoreCase(person.getVehicleType()))
                .toList();
    }

    @Override
    public Optional<DeliveryPerson> findByPhoneNumber(String phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        return store.values().stream()
                .filter(person -> phoneNumber.equals(person.getPhoneNumber()))
                .findFirst();
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .count();
    }

    @Override
    public long countAvailableByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return store.values().stream()
                .filter(person -> tenantId.equals(person.getTenantId()))
                .filter(person -> person.getStatus().isAvailable())
                .count();
    }

    @Override
    public void delete(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        store.remove(deliveryPersonId);
    }

    @Override
    public void delete(DeliveryPerson deliveryPerson) {
        Objects.requireNonNull(deliveryPerson, "Delivery person cannot be null");
        store.remove(deliveryPerson.getDeliveryPersonId());
    }

    @Override
    public boolean existsById(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return store.containsKey(deliveryPersonId);
    }
}
