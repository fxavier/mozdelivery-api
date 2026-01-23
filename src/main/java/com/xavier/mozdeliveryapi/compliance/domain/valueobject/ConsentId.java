package com.xavier.mozdeliveryapi.compliance.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Objects;
import java.util.UUID;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;


/**
 * Value object representing a consent identifier.
 */
public record ConsentId(UUID value) implements ValueObject {
    
    public ConsentId {
        Objects.requireNonNull(value, "Consent ID cannot be null");
    }
    
    public static ConsentId generate() {
        return new ConsentId(UUID.randomUUID());
    }
    
    public static ConsentId of(String value) {
        return new ConsentId(UUID.fromString(value));
    }
    
    public static ConsentId of(UUID value) {
        return new ConsentId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}