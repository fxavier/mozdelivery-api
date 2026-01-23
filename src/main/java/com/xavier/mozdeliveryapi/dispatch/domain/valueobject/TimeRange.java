package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.time.Instant;
import java.util.Objects;


/**
 * Value object representing a time range.
 */
public record TimeRange(
    Instant startTime,
    Instant endTime
) implements ValueObject {
    
    public TimeRange {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
    
    public static TimeRange of(Instant startTime, Instant endTime) {
        return new TimeRange(startTime, endTime);
    }
    
    public static TimeRange lastHour() {
        Instant now = Instant.now();
        return new TimeRange(now.minusSeconds(3600), now);
    }
    
    public static TimeRange lastDay() {
        Instant now = Instant.now();
        return new TimeRange(now.minusSeconds(86400), now);
    }
    
    public static TimeRange lastWeek() {
        Instant now = Instant.now();
        return new TimeRange(now.minusSeconds(604800), now);
    }
    
    /**
     * Check if the given timestamp is within this time range.
     */
    public boolean contains(Instant timestamp) {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        return !timestamp.isBefore(startTime) && !timestamp.isAfter(endTime);
    }
    
    /**
     * Get the duration of this time range.
     */
    public java.time.Duration getDuration() {
        return java.time.Duration.between(startTime, endTime);
    }
}