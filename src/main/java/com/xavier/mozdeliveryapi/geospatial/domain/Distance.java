package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Distance value object representing a distance measurement.
 * Internally stores distance in meters for precision.
 */
public final class Distance implements ValueObject, Comparable<Distance> {
    
    @NotNull
    @PositiveOrZero
    private final BigDecimal meters;
    
    private Distance(BigDecimal meters) {
        this.meters = meters.setScale(2, RoundingMode.HALF_UP);
    }
    
    public static Distance ofMeters(BigDecimal meters) {
        Objects.requireNonNull(meters, "Distance in meters cannot be null");
        if (meters.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        return new Distance(meters);
    }
    
    public static Distance ofMeters(double meters) {
        return ofMeters(BigDecimal.valueOf(meters));
    }
    
    public static Distance ofKilometers(BigDecimal kilometers) {
        Objects.requireNonNull(kilometers, "Distance in kilometers cannot be null");
        return ofMeters(kilometers.multiply(BigDecimal.valueOf(1000)));
    }
    
    public static Distance ofKilometers(double kilometers) {
        return ofKilometers(BigDecimal.valueOf(kilometers));
    }
    
    public static Distance zero() {
        return new Distance(BigDecimal.ZERO);
    }
    
    public BigDecimal getMeters() {
        return meters;
    }
    
    public BigDecimal getKilometers() {
        return meters.divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }
    
    public Distance add(Distance other) {
        Objects.requireNonNull(other, "Other distance cannot be null");
        return new Distance(this.meters.add(other.meters));
    }
    
    public Distance subtract(Distance other) {
        Objects.requireNonNull(other, "Other distance cannot be null");
        BigDecimal result = this.meters.subtract(other.meters);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result distance cannot be negative");
        }
        return new Distance(result);
    }
    
    public Distance multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Factor cannot be null");
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        return new Distance(this.meters.multiply(factor));
    }
    
    public boolean isGreaterThan(Distance other) {
        Objects.requireNonNull(other, "Other distance cannot be null");
        return this.meters.compareTo(other.meters) > 0;
    }
    
    public boolean isLessThan(Distance other) {
        Objects.requireNonNull(other, "Other distance cannot be null");
        return this.meters.compareTo(other.meters) < 0;
    }
    
    @Override
    public int compareTo(Distance other) {
        Objects.requireNonNull(other, "Other distance cannot be null");
        return this.meters.compareTo(other.meters);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return Objects.equals(meters, distance.meters);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(meters);
    }
    
    @Override
    public String toString() {
        if (meters.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return String.format("%.2f km", getKilometers().doubleValue());
        } else {
            return String.format("%.0f m", meters.doubleValue());
        }
    }
}