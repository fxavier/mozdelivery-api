package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Service interface for API key management.
 */
public interface ApiKeyService {
    
    /**
     * Create a new API key for a merchant.
     */
    CreateApiKeyResult createApiKey(String merchantId, String name, Set<String> scopes, 
                                   Instant expiresAt, String createdBy);
    
    /**
     * Validate an API key and return the associated key information.
     */
    ApiKeyValidationResult validateApiKey(String rawKey);
    
    /**
     * Revoke an API key.
     */
    void revokeApiKey(String keyId, String revokedBy);
    
    /**
     * Suspend an API key.
     */
    void suspendApiKey(String keyId, String suspendedBy);
    
    /**
     * Reactivate a suspended API key.
     */
    void reactivateApiKey(String keyId, String reactivatedBy);
    
    /**
     * List all API keys for a merchant.
     */
    List<ApiKey> listMerchantApiKeys(String merchantId);
    
    /**
     * Get API key details by ID.
     */
    ApiKey getApiKey(String keyId);
    
    /**
     * Record API key usage.
     */
    void recordUsage(String keyId);
    
    /**
     * Result of API key creation.
     */
    record CreateApiKeyResult(ApiKey apiKey, String rawKey) {}
    
    /**
     * Result of API key validation.
     */
    record ApiKeyValidationResult(boolean valid, ApiKey apiKey, String reason) {
        
        public static ApiKeyValidationResult valid(ApiKey apiKey) {
            return new ApiKeyValidationResult(true, apiKey, null);
        }
        
        public static ApiKeyValidationResult invalid(String reason) {
            return new ApiKeyValidationResult(false, null, reason);
        }
    }
}