package com.xavier.mozdeliveryapi.shared.domain.valueobject;

import java.util.UUID;

/**
 * Value object representing a unique user identifier.
 */
public record UserId(UUID value) implements ValueObject {
    
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
    
    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }
    
    public static UserId of(UUID value) {
        return new UserId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}