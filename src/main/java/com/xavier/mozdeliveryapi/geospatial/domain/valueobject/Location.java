package com.xavier.mozdeliveryapi.geospatial.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Location value object representing a geographic coordinate point.
 * Uses WGS84 coordinate system (EPSG:4326).
 */
public final class Location implements ValueObject {
    
    @NotNull
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
    private final BigDecimal latitude;
    
    @NotNull
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180 degrees")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180 degrees")
    private final BigDecimal longitude;
    
    private Location(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude.setScale(8, RoundingMode.HALF_UP);
        this.longitude = longitude.setScale(8, RoundingMode.HALF_UP);
    }
    
    public static Location of(BigDecimal latitude, BigDecimal longitude) {
        Objects.requireNonNull(latitude, "Latitude cannot be null");
        Objects.requireNonNull(longitude, "Longitude cannot be null");
        
        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0 || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        
        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0 || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        
        return new Location(latitude, longitude);
    }
    
    public static Location of(double latitude, double longitude) {
        return of(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    /**
     * Calculate the distance to another location using the Haversine formula.
     * @param other the other location
     * @return distance in meters
     */
    public Distance distanceTo(Location other) {
        Objects.requireNonNull(other, "Other location cannot be null");
        
        double lat1Rad = Math.toRadians(this.latitude.doubleValue());
        double lon1Rad = Math.toRadians(this.longitude.doubleValue());
        double lat2Rad = Math.toRadians(other.latitude.doubleValue());
        double lon2Rad = Math.toRadians(other.longitude.doubleValue());
        
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Earth's radius in meters
        double earthRadius = 6371000;
        double distanceInMeters = earthRadius * c;
        
        return Distance.ofMeters(BigDecimal.valueOf(distanceInMeters));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(latitude, location.latitude) &&
               Objects.equals(longitude, location.longitude);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
    
    @Override
    public String toString() {
        return String.format("Location{lat=%s, lon=%s}", latitude, longitude);
    }
}