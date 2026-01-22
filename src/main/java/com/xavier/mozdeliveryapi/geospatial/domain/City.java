package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * City value object representing a city where delivery services are available.
 * Cities are used to organize service areas and delivery operations.
 */
public final class City implements ValueObject {
    
    @NotBlank
    private final String name;
    
    @NotNull
    private final String countryCode;
    
    @NotNull
    private final Location centerLocation;
    
    private City(String name, String countryCode, Location centerLocation) {
        this.name = name.trim();
        this.countryCode = countryCode.toUpperCase().trim();
        this.centerLocation = centerLocation;
    }
    
    public static City of(String name, String countryCode, Location centerLocation) {
        Objects.requireNonNull(name, "City name cannot be null");
        Objects.requireNonNull(countryCode, "Country code cannot be null");
        Objects.requireNonNull(centerLocation, "Center location cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        
        if (countryCode.trim().length() != 2) {
            throw new IllegalArgumentException("Country code must be exactly 2 characters");
        }
        
        return new City(name, countryCode, centerLocation);
    }
    
    public String getName() {
        return name;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public Location getCenterLocation() {
        return centerLocation;
    }
    
    /**
     * Calculate distance from this city's center to a given location.
     */
    public Distance distanceTo(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        return centerLocation.distanceTo(location);
    }
    
    /**
     * Get a display name for the city including country.
     */
    public String getDisplayName() {
        return String.format("%s, %s", name, countryCode);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(name, city.name) &&
               Objects.equals(countryCode, city.countryCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, countryCode);
    }
    
    @Override
    public String toString() {
        return String.format("City{name='%s', countryCode='%s', center=%s}", 
                           name, countryCode, centerLocation);
    }
}