package com.xavier.mozdeliveryapi.shared.application.usecase.port;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for API key management.
 */
public interface ApiKeyRepository {
    
    /**
     * Save an API key.
     */
    ApiKey save(ApiKey apiKey);
    
    /**
     * Find an API key by its ID.
     */
    Optional<ApiKey> findByKeyId(String keyId);
    
    /**
     * Find an API key by its hashed value.
     */
    Optional<ApiKey> findByHashedKey(String hashedKey);
    
    /**
     * Find all API keys for a merchant.
     */
    List<ApiKey> findByMerchantId(String merchantId);
    
    /**
     * Find all active API keys for a merchant.
     */
    List<ApiKey> findActiveByMerchantId(String merchantId);
    
    /**
     * Delete an API key.
     */
    void delete(String keyId);
    
    /**
     * Update last used timestamp for an API key.
     */
    void updateLastUsed(String keyId, java.time.Instant lastUsed);
}