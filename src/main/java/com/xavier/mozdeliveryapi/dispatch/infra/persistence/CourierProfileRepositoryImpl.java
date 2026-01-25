package com.xavier.mozdeliveryapi.dispatch.infra.persistence;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.CourierProfileRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;

/**
 * In-memory implementation of CourierProfileRepository for development.
 */
@Repository
public class CourierProfileRepositoryImpl implements CourierProfileRepository {

    private final Map<DeliveryPersonId, CourierProfile> store = new ConcurrentHashMap<>();

    @Override
    public CourierProfile save(CourierProfile courierProfile) {
        Objects.requireNonNull(courierProfile, "Courier profile cannot be null");
        store.put(courierProfile.getDeliveryPersonId(), courierProfile);
        return courierProfile;
    }

    @Override
    public Optional<CourierProfile> findById(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return Optional.ofNullable(store.get(deliveryPersonId));
    }

    @Override
    public Optional<CourierProfile> findByUserId(UserId userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return store.values().stream()
                .filter(profile -> userId.equals(profile.getUserId()))
                .findFirst();
    }

    @Override
    public Optional<CourierProfile> findByEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        String normalizedEmail = email.trim().toLowerCase();
        return store.values().stream()
                .filter(profile -> normalizedEmail.equals(profile.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<CourierProfile> findByPhoneNumber(String phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        String normalizedPhone = phoneNumber.trim();
        return store.values().stream()
                .filter(profile -> normalizedPhone.equals(profile.getPhoneNumber()))
                .findFirst();
    }

    @Override
    public List<CourierProfile> findByApprovalStatus(CourierApprovalStatus approvalStatus) {
        Objects.requireNonNull(approvalStatus, "Approval status cannot be null");
        return store.values().stream()
                .filter(profile -> approvalStatus == profile.getApprovalStatus())
                .toList();
    }

    @Override
    public List<CourierProfile> findByStatus(DeliveryPersonStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        return store.values().stream()
                .filter(profile -> status == profile.getStatus())
                .toList();
    }

    @Override
    public List<CourierProfile> findByApprovalStatusAndStatus(CourierApprovalStatus approvalStatus,
                                                             DeliveryPersonStatus status) {
        Objects.requireNonNull(approvalStatus, "Approval status cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        return store.values().stream()
                .filter(profile -> approvalStatus == profile.getApprovalStatus())
                .filter(profile -> status == profile.getStatus())
                .toList();
    }

    @Override
    public List<CourierProfile> findByCity(String city) {
        Objects.requireNonNull(city, "City cannot be null");
        String normalizedCity = city.trim();
        return store.values().stream()
                .filter(profile -> normalizedCity.equals(profile.getCity()))
                .toList();
    }

    @Override
    public List<CourierProfile> findByApprovalStatusAndCity(CourierApprovalStatus approvalStatus, String city) {
        Objects.requireNonNull(approvalStatus, "Approval status cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        String normalizedCity = city.trim();
        return store.values().stream()
                .filter(profile -> approvalStatus == profile.getApprovalStatus())
                .filter(profile -> normalizedCity.equals(profile.getCity()))
                .toList();
    }

    @Override
    public List<CourierProfile> findAvailableInCity(String city) {
        Objects.requireNonNull(city, "City cannot be null");
        String normalizedCity = city.trim();
        return store.values().stream()
                .filter(profile -> normalizedCity.equals(profile.getCity()))
                .filter(CourierProfile::isAvailable)
                .toList();
    }

    @Override
    public long countByApprovalStatus(CourierApprovalStatus approvalStatus) {
        Objects.requireNonNull(approvalStatus, "Approval status cannot be null");
        return store.values().stream()
                .filter(profile -> approvalStatus == profile.getApprovalStatus())
                .count();
    }

    @Override
    public long countByCity(String city) {
        Objects.requireNonNull(city, "City cannot be null");
        String normalizedCity = city.trim();
        return store.values().stream()
                .filter(profile -> normalizedCity.equals(profile.getCity()))
                .count();
    }

    @Override
    public void delete(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        store.remove(deliveryPersonId);
    }

    @Override
    public void delete(CourierProfile courierProfile) {
        Objects.requireNonNull(courierProfile, "Courier profile cannot be null");
        store.remove(courierProfile.getDeliveryPersonId());
    }

    @Override
    public boolean existsById(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return store.containsKey(deliveryPersonId);
    }
}
