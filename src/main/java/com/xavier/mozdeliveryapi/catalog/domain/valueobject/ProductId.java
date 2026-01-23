package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a product identifier.
 */
public record ProductId(UUID value) implements ValueObject {
    
    public ProductId {
        Objects.requireNonNull(value, "Product ID cannot be null");
    }
    
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }
    
    public static ProductId of(String value) {
        return new ProductId(UUID.fromString(value));
    }
    
    public static ProductId of(UUID value) {
        return new ProductId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}