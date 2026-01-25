package com.xavier.mozdeliveryapi.shared.infra.persistence;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.shared.application.usecase.port.UserRepository;
import com.xavier.mozdeliveryapi.shared.domain.entity.User;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;

/**
 * In-memory implementation of UserRepository for development.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<UserId, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        store.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        String normalizedEmail = email.trim().toLowerCase();
        return store.values().stream()
                .filter(user -> normalizedEmail.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        return store.values().stream()
                .filter(user -> role == user.getRole())
                .toList();
    }

    @Override
    public List<User> findByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        return store.values().stream()
                .filter(user -> merchantId.equals(user.getMerchantId()))
                .toList();
    }

    @Override
    public List<User> findByRoleAndMerchantId(UserRole role, MerchantId merchantId) {
        Objects.requireNonNull(role, "Role cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        return store.values().stream()
                .filter(user -> role == user.getRole())
                .filter(user -> merchantId.equals(user.getMerchantId()))
                .toList();
    }

    @Override
    public List<User> findByActive(boolean active) {
        return store.values().stream()
                .filter(user -> user.isActive() == active)
                .toList();
    }

    @Override
    public long countByRole(UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        return store.values().stream()
                .filter(user -> role == user.getRole())
                .count();
    }

    @Override
    public long countByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        return store.values().stream()
                .filter(user -> merchantId.equals(user.getMerchantId()))
                .count();
    }

    @Override
    public void delete(UserId userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        store.remove(userId);
    }

    @Override
    public void delete(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        store.remove(user.getUserId());
    }

    @Override
    public boolean existsById(UserId userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return store.containsKey(userId);
    }
}
