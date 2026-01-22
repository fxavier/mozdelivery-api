package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an audit log identifier.
 */
public record AuditLogId(UUID value) implements ValueObject {
    
    public AuditLogId {
        Objects.requireNonNull(value, "Audit log ID cannot be null");
    }
    
    public static AuditLogId generate() {
        return new AuditLogId(UUID.randomUUID());
    }
    
    public static AuditLogId of(String value) {
        return new AuditLogId(UUID.fromString(value));
    }
    
    public static AuditLogId of(UUID value) {
        return new AuditLogId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}