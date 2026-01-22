package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TrafficConditions value object representing current traffic conditions
 * that affect route optimization and delivery time estimation.
 */
public final class TrafficConditions implements ValueObject {
    
    @NotNull
    private final TrafficLevel level;
    
    @NotNull
    @DecimalMin(value = "0.1", message = "Speed factor must be at least 0.1")
    @DecimalMax(value = "2.0", message = "Speed factor cannot exceed 2.0")
    private final BigDecimal speedFactor;
    
    @NotNull
    private final LocalDateTime timestamp;
    
    private final String description;
    
    private TrafficConditions(TrafficLevel level, BigDecimal speedFactor, LocalDateTime timestamp, String description) {
        this.level = Objects.requireNonNull(level, "Traffic level cannot be null");
        this.speedFactor = Objects.requireNonNull(speedFactor, "Speed factor cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.description = description;
        
        if (speedFactor.compareTo(BigDecimal.valueOf(0.1)) < 0 || speedFactor.compareTo(BigDecimal.valueOf(2.0)) > 0) {
            throw new IllegalArgumentException("Speed factor must be between 0.1 and 2.0");
        }
    }
    
    public static TrafficConditions of(TrafficLevel level, BigDecimal speedFactor, LocalDateTime timestamp) {
        return new TrafficConditions(level, speedFactor, timestamp, null);
    }
    
    public static TrafficConditions of(TrafficLevel level, BigDecimal speedFactor, LocalDateTime timestamp, String description) {
        return new TrafficConditions(level, speedFactor, timestamp, description);
    }
    
    /**
     * Create traffic conditions with current timestamp.
     */
    public static TrafficConditions current(TrafficLevel level, BigDecimal speedFactor) {
        return of(level, speedFactor, LocalDateTime.now());
    }
    
    /**
     * Create normal traffic conditions (no impact on speed).
     */
    public static TrafficConditions normal() {
        return current(TrafficLevel.NORMAL, BigDecimal.ONE);
    }
    
    /**
     * Create light traffic conditions (slightly faster than normal).
     */
    public static TrafficConditions light() {
        return current(TrafficLevel.LIGHT, BigDecimal.valueOf(1.2));
    }
    
    /**
     * Create heavy traffic conditions (slower than normal).
     */
    public static TrafficConditions heavy() {
        return current(TrafficLevel.HEAVY, BigDecimal.valueOf(0.6));
    }
    
    /**
     * Create severe traffic conditions (much slower).
     */
    public static TrafficConditions severe() {
        return current(TrafficLevel.SEVERE, BigDecimal.valueOf(0.3));
    }
    
    public TrafficLevel getLevel() {
        return level;
    }
    
    public BigDecimal getSpeedFactor() {
        return speedFactor;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getDescription() {
        return description != null ? description : level.getDescription();
    }
    
    /**
     * Apply traffic conditions to adjust a duration.
     * 
     * @param baseDuration the base duration without traffic impact
     * @return adjusted duration considering traffic conditions
     */
    public java.time.Duration adjustDuration(java.time.Duration baseDuration) {
        Objects.requireNonNull(baseDuration, "Base duration cannot be null");
        
        // Inverse relationship: lower speed factor means longer duration
        double adjustmentFactor = BigDecimal.ONE.divide(speedFactor, 2, java.math.RoundingMode.HALF_UP).doubleValue();
        long adjustedMinutes = Math.round(baseDuration.toMinutes() * adjustmentFactor);
        
        return java.time.Duration.ofMinutes(adjustedMinutes);
    }
    
    /**
     * Check if traffic conditions are favorable (better than normal).
     */
    public boolean isFavorable() {
        return speedFactor.compareTo(BigDecimal.ONE) > 0;
    }
    
    /**
     * Check if traffic conditions are adverse (worse than normal).
     */
    public boolean isAdverse() {
        return speedFactor.compareTo(BigDecimal.ONE) < 0;
    }
    
    /**
     * Get the percentage impact on travel time.
     * Positive values mean longer travel time, negative values mean shorter.
     */
    public int getTimeImpactPercentage() {
        BigDecimal impact = BigDecimal.ONE.divide(speedFactor, 2, java.math.RoundingMode.HALF_UP)
                                        .subtract(BigDecimal.ONE)
                                        .multiply(BigDecimal.valueOf(100));
        return impact.intValue();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrafficConditions that = (TrafficConditions) o;
        return level == that.level &&
               Objects.equals(speedFactor, that.speedFactor) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(description, that.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(level, speedFactor, timestamp, description);
    }
    
    @Override
    public String toString() {
        return String.format("TrafficConditions{level=%s, speedFactor=%s, impact=%+d%%}", 
                           level, speedFactor, getTimeImpactPercentage());
    }
}