package com.xavier.mozdeliveryapi.geospatial.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;
import com.xavier.mozdeliveryapi.geospatial.domain.entity.ServiceArea;

/**
 * ServiceAreaId value object representing a unique identifier for a service area.
 */
public final class ServiceAreaId implements ValueObject {
    
    @NotNull
    private final UUID value;
    
    private ServiceAreaId(UUID value) {
        this.value = Objects.requireNonNull(value, "ServiceArea ID value cannot be null");
    }
    
    public static ServiceAreaId of(UUID value) {
        return new ServiceAreaId(value);
    }
    
    public static ServiceAreaId of(String value) {
        Objects.requireNonNull(value, "ServiceArea ID string cannot be null");
        try {
            return new ServiceAreaId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ServiceArea ID format: " + value, e);
        }
    }
    
    public static ServiceAreaId generate() {
        return new ServiceAreaId(UUID.randomUUID());
    }
    
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceAreaId that = (ServiceAreaId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}