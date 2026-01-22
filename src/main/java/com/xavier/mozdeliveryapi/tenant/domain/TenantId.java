package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a tenant identifier.
 */
public record TenantId(UUID value) implements ValueObject {
    
    public TenantId {
        Objects.requireNonNull(value, "Tenant ID cannot be null");
    }
    
    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }
    
    public static TenantId of(String value) {
        return new TenantId(UUID.fromString(value));
    }
    
    public static TenantId of(UUID value) {
        return new TenantId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}