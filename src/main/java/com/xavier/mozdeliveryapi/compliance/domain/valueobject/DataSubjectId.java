package com.xavier.mozdeliveryapi.compliance.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import java.util.UUID;


/**
 * Value object representing a data subject identifier (customer, user, etc.).
 */
public record DataSubjectId(UUID value) implements ValueObject {
    
    public DataSubjectId {
        Objects.requireNonNull(value, "Data subject ID cannot be null");
    }
    
    public static DataSubjectId generate() {
        return new DataSubjectId(UUID.randomUUID());
    }
    
    public static DataSubjectId of(String value) {
        return new DataSubjectId(UUID.fromString(value));
    }
    
    public static DataSubjectId of(UUID value) {
        return new DataSubjectId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}