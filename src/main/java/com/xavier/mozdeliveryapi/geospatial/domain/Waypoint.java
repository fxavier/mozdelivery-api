package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Waypoint value object representing a point in a route with additional metadata.
 * Each waypoint has a location, type, and optional stop duration.
 */
public final class Waypoint implements ValueObject {
    
    @NotNull
    private final Location location;
    
    @NotNull
    private final WaypointType type;
    
    private final Duration stopDuration;
    private final String description;
    
    private Waypoint(Location location, WaypointType type, Duration stopDuration, String description) {
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        this.type = Objects.requireNonNull(type, "Waypoint type cannot be null");
        this.stopDuration = stopDuration;
        this.description = description;
    }
    
    public static Waypoint of(Location location, WaypointType type) {
        return new Waypoint(location, type, null, null);
    }
    
    public static Waypoint of(Location location, WaypointType type, Duration stopDuration) {
        return new Waypoint(location, type, stopDuration, null);
    }
    
    public static Waypoint of(Location location, WaypointType type, Duration stopDuration, String description) {
        return new Waypoint(location, type, stopDuration, description);
    }
    
    /**
     * Create a start waypoint.
     */
    public static Waypoint start(Location location) {
        return of(location, WaypointType.START);
    }
    
    /**
     * Create an end waypoint.
     */
    public static Waypoint end(Location location) {
        return of(location, WaypointType.END);
    }
    
    /**
     * Create an intermediate waypoint with stop duration.
     */
    public static Waypoint intermediate(Location location, Duration stopDuration) {
        return of(location, WaypointType.INTERMEDIATE, stopDuration);
    }
    
    /**
     * Create a delivery waypoint with stop duration and description.
     */
    public static Waypoint delivery(Location location, Duration stopDuration, String description) {
        return of(location, WaypointType.DELIVERY, stopDuration, description);
    }
    
    public Location getLocation() {
        return location;
    }
    
    public WaypointType getType() {
        return type;
    }
    
    public Optional<Duration> getStopDuration() {
        return Optional.ofNullable(stopDuration);
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    /**
     * Check if this waypoint requires a stop (has stop duration).
     */
    public boolean requiresStop() {
        return stopDuration != null && !stopDuration.isZero();
    }
    
    /**
     * Check if this is a delivery waypoint.
     */
    public boolean isDelivery() {
        return type == WaypointType.DELIVERY;
    }
    
    /**
     * Check if this is the start of the route.
     */
    public boolean isStart() {
        return type == WaypointType.START;
    }
    
    /**
     * Check if this is the end of the route.
     */
    public boolean isEnd() {
        return type == WaypointType.END;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(location, waypoint.location) &&
               type == waypoint.type &&
               Objects.equals(stopDuration, waypoint.stopDuration) &&
               Objects.equals(description, waypoint.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(location, type, stopDuration, description);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Waypoint{")
          .append("type=").append(type)
          .append(", location=").append(location);
        
        if (stopDuration != null) {
            sb.append(", stopDuration=").append(stopDuration);
        }
        
        if (description != null) {
            sb.append(", description='").append(description).append("'");
        }
        
        sb.append("}");
        return sb.toString();
    }
}