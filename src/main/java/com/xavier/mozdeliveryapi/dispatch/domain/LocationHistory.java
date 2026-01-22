package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a historical location record.
 */
public record LocationHistory(
    Location location,
    Instant timestamp,
    double accuracy,
    double speed,
    String source
) implements ValueObject {
    
    public LocationHistory {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        
        if (accuracy < 0) {
            throw new IllegalArgumentException("Accuracy cannot be negative");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        // source can be null
    }
    
    public static LocationHistory of(Location location, Instant timestamp, double accuracy, double speed) {
        return new LocationHistory(location, timestamp, accuracy, speed, null);
    }
    
    public static LocationHistory of(Location location, Instant timestamp, double accuracy, double speed, String source) {
        return new LocationHistory(location, timestamp, accuracy, speed, source);
    }
    
    public static LocationHistory of(Location location, double accuracy) {
        return new LocationHistory(location, Instant.now(), accuracy, 0.0, null);
    }
    
    /**
     * Check if this location record is recent (within the last 5 minutes).
     */
    public boolean isRecent() {
        return timestamp.isAfter(Instant.now().minusSeconds(300)); // 5 minutes
    }
    
    /**
     * Check if this location record has good accuracy (less than 50 meters).
     */
    public boolean hasGoodAccuracy() {
        return accuracy <= 50.0;
    }
}