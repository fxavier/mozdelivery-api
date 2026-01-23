package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a catalog identifier.
 */
public record CatalogId(UUID value) implements ValueObject {
    
    public CatalogId {
        Objects.requireNonNull(value, "Catalog ID cannot be null");
    }
    
    public static CatalogId generate() {
        return new CatalogId(UUID.randomUUID());
    }
    
    public static CatalogId of(String value) {
        return new CatalogId(UUID.fromString(value));
    }
    
    public static CatalogId of(UUID value) {
        return new CatalogId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}