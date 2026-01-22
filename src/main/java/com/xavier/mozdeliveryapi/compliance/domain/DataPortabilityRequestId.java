package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a data portability request identifier.
 */
public record DataPortabilityRequestId(UUID value) implements ValueObject {
    
    public DataPortabilityRequestId {
        Objects.requireNonNull(value, "Data portability request ID cannot be null");
    }
    
    public static DataPortabilityRequestId generate() {
        return new DataPortabilityRequestId(UUID.randomUUID());
    }
    
    public static DataPortabilityRequestId of(String value) {
        return new DataPortabilityRequestId(UUID.fromString(value));
    }
    
    public static DataPortabilityRequestId of(UUID value) {
        return new DataPortabilityRequestId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}