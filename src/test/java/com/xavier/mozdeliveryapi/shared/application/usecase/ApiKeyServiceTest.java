package com.xavier.mozdeliveryapi.shared.application.usecase;

import com.xavier.mozdeliveryapi.shared.application.usecase.port.ApiKeyRepository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus;
import com.xavier.mozdeliveryapi.shared.infra.persistence.InMemoryApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for API key service.
 */
class ApiKeyServiceTest {
    
    private ApiKeyService apiKeyService;
    private ApiKeyRepository apiKeyRepository;
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        apiKeyRepository = new InMemoryApiKeyRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        apiKeyService = new ApiKeyServiceImpl(apiKeyRepository, passwordEncoder);
    }
    
    @Test
    @DisplayName("Should create API key successfully")
    void shouldCreateApiKeySuccessfully() {
        // Given
        String merchantId = "merchant-123";
        String name = "Test API Key";
        Set<String> scopes = Set.of("catalog:read", "orders:create");
        Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
        String createdBy = "admin-user";
        
        // When
        ApiKeyService.CreateApiKeyResult result = apiKeyService.createApiKey(
            merchantId, name, scopes, expiresAt, createdBy
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.apiKey()).isNotNull();
        assertThat(result.rawKey()).isNotNull();
        
        ApiKey apiKey = result.apiKey();
        assertThat(apiKey.merchantId()).isEqualTo(merchantId);
        assertThat(apiKey.name()).isEqualTo(name);
        assertThat(apiKey.scopes()).isEqualTo(scopes);
        assertThat(apiKey.status()).isEqualTo(ApiKeyStatus.ACTIVE);
        assertThat(apiKey.expiresAt()).isEqualTo(expiresAt);
        assertThat(apiKey.createdBy()).isEqualTo(createdBy);
        
        // Raw key should have correct prefix
        assertThat(result.rawKey()).startsWith("mk_");
    }
    
    @Test
    @DisplayName("Should validate API key successfully")
    void shouldValidateApiKeySuccessfully() {
        // Given
        String merchantId = "merchant-123";
        String name = "Test API Key";
        Set<String> scopes = Set.of("catalog:read");
        String createdBy = "admin-user";
        
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, null, createdBy
        );
        String rawKey = createResult.rawKey();
        
        // When
        ApiKeyService.ApiKeyValidationResult validationResult = apiKeyService.validateApiKey(rawKey);
        
        // Then
        assertThat(validationResult.valid()).isTrue();
        assertThat(validationResult.apiKey()).isNotNull();
        assertThat(validationResult.apiKey().merchantId()).isEqualTo(merchantId);
        assertThat(validationResult.reason()).isNull();
    }
    
    @Test
    @DisplayName("Should reject invalid API key")
    void shouldRejectInvalidApiKey() {
        // Given
        String invalidKey = "invalid-key";
        
        // When
        ApiKeyService.ApiKeyValidationResult result = apiKeyService.validateApiKey(invalidKey);
        
        // Then
        assertThat(result.valid()).isFalse();
        assertThat(result.apiKey()).isNull();
        assertThat(result.reason()).isEqualTo("Invalid API key format");
    }
    
    @Test
    @DisplayName("Should reject expired API key")
    void shouldRejectExpiredApiKey() {
        // Given
        String merchantId = "merchant-123";
        String name = "Expired API Key";
        Set<String> scopes = Set.of("catalog:read");
        Instant expiresAt = Instant.now().minus(1, ChronoUnit.DAYS); // Expired
        String createdBy = "admin-user";
        
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, expiresAt, createdBy
        );
        String rawKey = createResult.rawKey();
        
        // When
        ApiKeyService.ApiKeyValidationResult result = apiKeyService.validateApiKey(rawKey);
        
        // Then
        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).isEqualTo("API key has expired");
    }
    
    @Test
    @DisplayName("Should revoke API key successfully")
    void shouldRevokeApiKeySuccessfully() {
        // Given
        String merchantId = "merchant-123";
        String name = "Test API Key";
        Set<String> scopes = Set.of("catalog:read");
        String createdBy = "admin-user";
        
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, null, createdBy
        );
        String keyId = createResult.apiKey().keyId();
        String rawKey = createResult.rawKey();
        
        // When
        apiKeyService.revokeApiKey(keyId, "admin-user");
        
        // Then
        ApiKey revokedKey = apiKeyService.getApiKey(keyId);
        assertThat(revokedKey.status()).isEqualTo(ApiKeyStatus.REVOKED);
        
        // Should not validate revoked key
        ApiKeyService.ApiKeyValidationResult result = apiKeyService.validateApiKey(rawKey);
        assertThat(result.valid()).isFalse();
        assertThat(result.reason()).isEqualTo("API key is not active");
    }
    
    @Test
    @DisplayName("Should suspend and reactivate API key")
    void shouldSuspendAndReactivateApiKey() {
        // Given
        String merchantId = "merchant-123";
        String name = "Test API Key";
        Set<String> scopes = Set.of("catalog:read");
        String createdBy = "admin-user";
        
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, null, createdBy
        );
        String keyId = createResult.apiKey().keyId();
        String rawKey = createResult.rawKey();
        
        // When - suspend
        apiKeyService.suspendApiKey(keyId, "admin-user");
        
        // Then - should be suspended
        ApiKey suspendedKey = apiKeyService.getApiKey(keyId);
        assertThat(suspendedKey.status()).isEqualTo(ApiKeyStatus.SUSPENDED);
        
        ApiKeyService.ApiKeyValidationResult suspendedResult = apiKeyService.validateApiKey(rawKey);
        assertThat(suspendedResult.valid()).isFalse();
        
        // When - reactivate
        apiKeyService.reactivateApiKey(keyId, "admin-user");
        
        // Then - should be active again
        ApiKey reactivatedKey = apiKeyService.getApiKey(keyId);
        assertThat(reactivatedKey.status()).isEqualTo(ApiKeyStatus.ACTIVE);
        
        ApiKeyService.ApiKeyValidationResult reactivatedResult = apiKeyService.validateApiKey(rawKey);
        assertThat(reactivatedResult.valid()).isTrue();
    }
    
    @Test
    @DisplayName("Should not reactivate revoked API key")
    void shouldNotReactivateRevokedApiKey() {
        // Given
        String merchantId = "merchant-123";
        String name = "Test API Key";
        Set<String> scopes = Set.of("catalog:read");
        String createdBy = "admin-user";
        
        ApiKeyService.CreateApiKeyResult createResult = apiKeyService.createApiKey(
            merchantId, name, scopes, null, createdBy
        );
        String keyId = createResult.apiKey().keyId();
        
        // Revoke the key
        apiKeyService.revokeApiKey(keyId, "admin-user");
        
        // When/Then - should throw exception
        assertThatThrownBy(() -> apiKeyService.reactivateApiKey(keyId, "admin-user"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot reactivate revoked API key");
    }
    
    @Test
    @DisplayName("Should list merchant API keys")
    void shouldListMerchantApiKeys() {
        // Given
        String merchantId = "merchant-123";
        String otherMerchantId = "merchant-456";
        
        // Create keys for first merchant
        apiKeyService.createApiKey(merchantId, "Key 1", Set.of("catalog:read"), null, "admin");
        apiKeyService.createApiKey(merchantId, "Key 2", Set.of("orders:create"), null, "admin");
        
        // Create key for other merchant
        apiKeyService.createApiKey(otherMerchantId, "Other Key", Set.of("catalog:read"), null, "admin");
        
        // When
        var merchantKeys = apiKeyService.listMerchantApiKeys(merchantId);
        
        // Then
        assertThat(merchantKeys).hasSize(2);
        assertThat(merchantKeys).allMatch(key -> key.merchantId().equals(merchantId));
    }
    
    @Test
    @DisplayName("Should require valid parameters for API key creation")
    void shouldRequireValidParametersForApiKeyCreation() {
        // Test null merchant ID
        assertThatThrownBy(() -> apiKeyService.createApiKey(null, "name", Set.of("scope"), null, "user"))
            .isInstanceOf(NullPointerException.class);
        
        // Test null name
        assertThatThrownBy(() -> apiKeyService.createApiKey("merchant", null, Set.of("scope"), null, "user"))
            .isInstanceOf(NullPointerException.class);
        
        // Test empty scopes
        assertThatThrownBy(() -> apiKeyService.createApiKey("merchant", "name", Set.of(), null, "user"))
            .isInstanceOf(IllegalArgumentException.class);
        
        // Test null created by
        assertThatThrownBy(() -> apiKeyService.createApiKey("merchant", "name", Set.of("scope"), null, null))
            .isInstanceOf(NullPointerException.class);
    }
}