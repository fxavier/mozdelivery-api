package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a data deletion request identifier.
 */
public record DataDeletionRequestId(UUID value) implements ValueObject {
    
    public DataDeletionRequestId {
        Objects.requireNonNull(value, "Data deletion request ID cannot be null");
    }
    
    public static DataDeletionRequestId generate() {
        return new DataDeletionRequestId(UUID.randomUUID());
    }
    
    public static DataDeletionRequestId of(String value) {
        return new DataDeletionRequestId(UUID.fromString(value));
    }
    
    public static DataDeletionRequestId of(UUID value) {
        return new DataDeletionRequestId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}