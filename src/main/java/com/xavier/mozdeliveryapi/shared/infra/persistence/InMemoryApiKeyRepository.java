package com.xavier.mozdeliveryapi.shared.infra.persistence;

import com.xavier.mozdeliveryapi.shared.application.usecase.port.ApiKeyRepository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of API key repository for development and testing.
 * In production, this should be replaced with a proper database implementation.
 */
@Repository
public class InMemoryApiKeyRepository implements ApiKeyRepository {
    
    private final Map<String, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final Map<String, String> hashedKeyToKeyId = new ConcurrentHashMap<>();
    
    @Override
    public ApiKey save(ApiKey apiKey) {
        apiKeys.put(apiKey.keyId(), apiKey);
        hashedKeyToKeyId.put(apiKey.hashedKey(), apiKey.keyId());
        return apiKey;
    }
    
    @Override
    public Optional<ApiKey> findByKeyId(String keyId) {
        return Optional.ofNullable(apiKeys.get(keyId));
    }
    
    @Override
    public Optional<ApiKey> findByHashedKey(String hashedKey) {
        String keyId = hashedKeyToKeyId.get(hashedKey);
        if (keyId != null) {
            return Optional.ofNullable(apiKeys.get(keyId));
        }
        return Optional.empty();
    }
    
    @Override
    public List<ApiKey> findByMerchantId(String merchantId) {
        return apiKeys.values().stream()
            .filter(apiKey -> apiKey.merchantId().equals(merchantId))
            .toList();
    }
    
    @Override
    public List<ApiKey> findActiveByMerchantId(String merchantId) {
        return apiKeys.values().stream()
            .filter(apiKey -> apiKey.merchantId().equals(merchantId))
            .filter(ApiKey::isActive)
            .toList();
    }
    
    @Override
    public void delete(String keyId) {
        ApiKey apiKey = apiKeys.remove(keyId);
        if (apiKey != null) {
            hashedKeyToKeyId.remove(apiKey.hashedKey());
        }
    }
    
    @Override
    public void updateLastUsed(String keyId, Instant lastUsed) {
        ApiKey apiKey = apiKeys.get(keyId);
        if (apiKey != null) {
            ApiKey updatedKey = apiKey.withLastUsed(lastUsed);
            apiKeys.put(keyId, updatedKey);
        }
    }
    
    /**
     * Get all API keys (for validation purposes).
     * This method is used by the API key service for validation.
     */
    public List<ApiKey> findAllActive() {
        return apiKeys.values().stream()
            .filter(ApiKey::isActive)
            .toList();
    }
    
    /**
     * Get all API keys regardless of status (for validation purposes).
     */
    public List<ApiKey> findAll() {
        return List.copyOf(apiKeys.values());
    }
}