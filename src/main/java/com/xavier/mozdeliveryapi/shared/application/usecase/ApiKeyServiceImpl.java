package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.application.usecase.port.ApiKeyRepository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of API key management service.
 */
@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyServiceImpl.class);
    private static final int API_KEY_LENGTH = 32;
    private static final String API_KEY_PREFIX = "mk_"; // merchant key prefix
    
    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    
    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = new SecureRandom();
    }
    
    @Override
    public CreateApiKeyResult createApiKey(String merchantId, String name, Set<String> scopes, 
                                          Instant expiresAt, String createdBy) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(scopes, "Scopes cannot be null");
        Objects.requireNonNull(createdBy, "Created by cannot be null");
        
        if (scopes.isEmpty()) {
            throw new IllegalArgumentException("API key must have at least one scope");
        }
        
        // Generate raw API key
        String rawKey = generateRawApiKey();
        String keyId = generateKeyId();
        String hashedKey = passwordEncoder.encode(rawKey);
        
        ApiKey apiKey = new ApiKey(
            keyId,
            hashedKey,
            merchantId,
            name,
            Set.copyOf(scopes),
            ApiKeyStatus.ACTIVE,
            Instant.now(),
            expiresAt,
            null,
            createdBy
        );
        
        ApiKey savedKey = apiKeyRepository.save(apiKey);
        
        logger.info("Created API key {} for merchant {} with scopes {}", 
                   keyId, merchantId, scopes);
        
        return new CreateApiKeyResult(savedKey, rawKey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ApiKeyValidationResult validateApiKey(String rawKey) {
        if (rawKey == null || rawKey.trim().isEmpty()) {
            return ApiKeyValidationResult.invalid("API key is required");
        }
        
        if (!rawKey.startsWith(API_KEY_PREFIX)) {
            return ApiKeyValidationResult.invalid("Invalid API key format");
        }
        
        // Get all active keys and check against them
        List<ApiKey> allActiveKeys = findAllActiveKeys();
        
        for (ApiKey apiKey : allActiveKeys) {
            if (passwordEncoder.matches(rawKey, apiKey.hashedKey())) {
                // Check expiration first (before checking status)
                if (apiKey.isExpired()) {
                    return ApiKeyValidationResult.invalid("API key has expired");
                }
                
                // Then check status
                if (apiKey.status() != ApiKeyStatus.ACTIVE) {
                    return ApiKeyValidationResult.invalid("API key is not active");
                }
                
                // Record usage asynchronously
                recordUsage(apiKey.keyId());
                
                return ApiKeyValidationResult.valid(apiKey);
            }
        }
        
        return ApiKeyValidationResult.invalid("Invalid API key");
    }
    
    @Override
    public void revokeApiKey(String keyId, String revokedBy) {
        Objects.requireNonNull(keyId, "Key ID cannot be null");
        Objects.requireNonNull(revokedBy, "Revoked by cannot be null");
        
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> new IllegalArgumentException("API key not found: " + keyId));
        
        ApiKey revokedKey = apiKey.withStatus(ApiKeyStatus.REVOKED);
        apiKeyRepository.save(revokedKey);
        
        logger.info("Revoked API key {} by {}", keyId, revokedBy);
    }
    
    @Override
    public void suspendApiKey(String keyId, String suspendedBy) {
        Objects.requireNonNull(keyId, "Key ID cannot be null");
        Objects.requireNonNull(suspendedBy, "Suspended by cannot be null");
        
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> new IllegalArgumentException("API key not found: " + keyId));
        
        ApiKey suspendedKey = apiKey.withStatus(ApiKeyStatus.SUSPENDED);
        apiKeyRepository.save(suspendedKey);
        
        logger.info("Suspended API key {} by {}", keyId, suspendedBy);
    }
    
    @Override
    public void reactivateApiKey(String keyId, String reactivatedBy) {
        Objects.requireNonNull(keyId, "Key ID cannot be null");
        Objects.requireNonNull(reactivatedBy, "Reactivated by cannot be null");
        
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> new IllegalArgumentException("API key not found: " + keyId));
        
        if (apiKey.status() == ApiKeyStatus.REVOKED) {
            throw new IllegalStateException("Cannot reactivate revoked API key");
        }
        
        ApiKey reactivatedKey = apiKey.withStatus(ApiKeyStatus.ACTIVE);
        apiKeyRepository.save(reactivatedKey);
        
        logger.info("Reactivated API key {} by {}", keyId, reactivatedBy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ApiKey> listMerchantApiKeys(String merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        return apiKeyRepository.findByMerchantId(merchantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ApiKey getApiKey(String keyId) {
        Objects.requireNonNull(keyId, "Key ID cannot be null");
        return apiKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> new IllegalArgumentException("API key not found: " + keyId));
    }
    
    @Override
    public void recordUsage(String keyId) {
        try {
            apiKeyRepository.updateLastUsed(keyId, Instant.now());
        } catch (Exception e) {
            logger.warn("Failed to record API key usage for {}: {}", keyId, e.getMessage());
        }
    }
    
    private String generateRawApiKey() {
        byte[] keyBytes = new byte[API_KEY_LENGTH];
        secureRandom.nextBytes(keyBytes);
        String encodedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
        return API_KEY_PREFIX + encodedKey;
    }
    
    private String generateKeyId() {
        byte[] idBytes = new byte[16];
        secureRandom.nextBytes(idBytes);
        return "key_" + Base64.getUrlEncoder().withoutPadding().encodeToString(idBytes);
    }
    
    private List<ApiKey> findAllActiveKeys() {
        // Use the repository method to find all keys for validation
        if (apiKeyRepository instanceof com.xavier.mozdeliveryapi.shared.infra.persistence.InMemoryApiKeyRepository inMemoryRepo) {
            // Get all keys, not just active ones, because we need to check status during validation
            return inMemoryRepo.findAll();
        }
        
        // Fallback for other repository implementations
        throw new UnsupportedOperationException("Finding all keys not supported by this repository implementation");
    }
}