package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a category identifier.
 */
public record CategoryId(UUID value) implements ValueObject {
    
    public CategoryId {
        Objects.requireNonNull(value, "Category ID cannot be null");
    }
    
    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }
    
    public static CategoryId of(String value) {
        return new CategoryId(UUID.fromString(value));
    }
    
    public static CategoryId of(UUID value) {
        return new CategoryId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}