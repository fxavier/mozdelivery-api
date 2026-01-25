package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.application.usecase.ApiKeyService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for API security features.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApiSecurityIntegrationTest {
    
    @Autowired
    private ApiKeyService apiKeyService;
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    @Test
    @DisplayName("Should create and validate API key successfully")
    void shouldCreateAndValidateApiKeySuccessfully() {
        // Given
        String merchantId = "test-merchant";
        String name = "Integration Test Key";
        Set<String> scopes = Set.of("catalog:read", "orders:create");
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
        String createdBy = "test-user";
        
        // When - Create API key
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, expiresAt, createdBy
        );
        
        // Then - API key should be created
        assertThat(createResult).isNotNull();
        assertThat(createResult.apiKey()).isNotNull();
        assertThat(createResult.rawKey()).isNotNull();
        assertThat(createResult.apiKey().status()).isEqualTo(ApiKeyStatus.ACTIVE);
        
        // When - Validate API key
        ApiKeyService.ApiKeyValidationResult validationResult = 
            apiKeyService.validateApiKey(createResult.rawKey());
        
        // Then - API key should be valid
        assertThat(validationResult.valid()).isTrue();
        assertThat(validationResult.apiKey()).isNotNull();
        assertThat(validationResult.apiKey().merchantId()).isEqualTo(merchantId);
        assertThat(validationResult.apiKey().scopes()).isEqualTo(scopes);
    }
    
    @Test
    @DisplayName("Should apply rate limiting correctly")
    void shouldApplyRateLimitingCorrectly() {
        // Given
        String key = "test-key";
        int limit = 5;
        
        // When - Make requests within limit
        for (int i = 0; i < limit; i++) {
            boolean allowed = rateLimitingService.isAllowed(key, limit);
            assertThat(allowed).isTrue();
        }
        
        // Then - Next request should be rate limited
        boolean rateLimited = rateLimitingService.isAllowed(key, limit);
        assertThat(rateLimited).isFalse();
        
        // And - Should have correct remaining tokens
        long remaining = rateLimitingService.getRemainingTokens(key, limit);
        assertThat(remaining).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should handle API key lifecycle correctly")
    void shouldHandleApiKeyLifecycleCorrectly() {
        // Given
        String merchantId = "lifecycle-test-merchant";
        String name = "Lifecycle Test Key";
        Set<String> scopes = Set.of("catalog:read");
        String createdBy = "test-user";
        
        // When - Create API key
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, null, createdBy
        );
        String keyId = createResult.apiKey().keyId();
        String rawKey = createResult.rawKey();
        
        // Then - Should be valid initially
        assertThat(apiKeyService.validateApiKey(rawKey).valid()).isTrue();
        
        // When - Suspend API key
        apiKeyService.suspendApiKey(keyId, "admin");
        
        // Then - Should be invalid when suspended
        ApiKeyService.ApiKeyValidationResult suspendedResult = apiKeyService.validateApiKey(rawKey);
        assertThat(suspendedResult.valid()).isFalse();
        assertThat(suspendedResult.reason()).isEqualTo("API key is not active");
        
        // When - Reactivate API key
        apiKeyService.reactivateApiKey(keyId, "admin");
        
        // Then - Should be valid again
        assertThat(apiKeyService.validateApiKey(rawKey).valid()).isTrue();
        
        // When - Revoke API key
        apiKeyService.revokeApiKey(keyId, "admin");
        
        // Then - Should be invalid when revoked
        ApiKeyService.ApiKeyValidationResult revokedResult = apiKeyService.validateApiKey(rawKey);
        assertThat(revokedResult.valid()).isFalse();
        assertThat(revokedResult.reason()).isEqualTo("API key is not active");
    }
}