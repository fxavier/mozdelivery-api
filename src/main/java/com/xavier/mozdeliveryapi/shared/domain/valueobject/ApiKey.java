package com.xavier.mozdeliveryapi.shared.domain.valueobject;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Value object representing an API key for merchant integrations.
 */
public record ApiKey(
    String keyId,
    String hashedKey,
    String merchantId,
    String name,
    Set<String> scopes,
    ApiKeyStatus status,
    Instant createdAt,
    Instant expiresAt,
    Instant lastUsedAt,
    String createdBy
) {
    
    public ApiKey {
        Objects.requireNonNull(keyId, "Key ID cannot be null");
        Objects.requireNonNull(hashedKey, "Hashed key cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(scopes, "Scopes cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(createdAt, "Created at cannot be null");
        Objects.requireNonNull(createdBy, "Created by cannot be null");
        
        if (scopes.isEmpty()) {
            throw new IllegalArgumentException("API key must have at least one scope");
        }
    }
    
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
    
    public boolean isActive() {
        return status == ApiKeyStatus.ACTIVE && !isExpired();
    }
    
    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }
    
    public ApiKey withLastUsed(Instant lastUsed) {
        return new ApiKey(
            keyId, hashedKey, merchantId, name, scopes, status,
            createdAt, expiresAt, lastUsed, createdBy
        );
    }
    
    public ApiKey withStatus(ApiKeyStatus newStatus) {
        return new ApiKey(
            keyId, hashedKey, merchantId, name, scopes, newStatus,
            createdAt, expiresAt, lastUsedAt, createdBy
        );
    }
}