package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing vehicle information for a courier.
 */
public record VehicleInfo(
    String type,
    String make,
    String model,
    String licensePlate,
    String color,
    int year
) {
    
    public VehicleInfo {
        Objects.requireNonNull(type, "Vehicle type cannot be null");
        Objects.requireNonNull(make, "Vehicle make cannot be null");
        Objects.requireNonNull(model, "Vehicle model cannot be null");
        Objects.requireNonNull(licensePlate, "License plate cannot be null");
        Objects.requireNonNull(color, "Vehicle color cannot be null");
        
        if (type.isBlank()) {
            throw new IllegalArgumentException("Vehicle type cannot be blank");
        }
        if (make.isBlank()) {
            throw new IllegalArgumentException("Vehicle make cannot be blank");
        }
        if (model.isBlank()) {
            throw new IllegalArgumentException("Vehicle model cannot be blank");
        }
        if (licensePlate.isBlank()) {
            throw new IllegalArgumentException("License plate cannot be blank");
        }
        if (color.isBlank()) {
            throw new IllegalArgumentException("Vehicle color cannot be blank");
        }
        if (year < 1900 || year > 2030) {
            throw new IllegalArgumentException("Invalid vehicle year: " + year);
        }
    }
    
    /**
     * Get vehicle display name.
     */
    public String getDisplayName() {
        return String.format("%d %s %s (%s)", year, make, model, licensePlate);
    }
    
    /**
     * Check if this is a motorcycle.
     */
    public boolean isMotorcycle() {
        return "MOTORCYCLE".equalsIgnoreCase(type) || "BIKE".equalsIgnoreCase(type);
    }
    
    /**
     * Check if this is a car.
     */
    public boolean isCar() {
        return "CAR".equalsIgnoreCase(type) || "SEDAN".equalsIgnoreCase(type) || 
               "HATCHBACK".equalsIgnoreCase(type) || "SUV".equalsIgnoreCase(type);
    }
    
    /**
     * Check if this is a bicycle.
     */
    public boolean isBicycle() {
        return "BICYCLE".equalsIgnoreCase(type);
    }
}