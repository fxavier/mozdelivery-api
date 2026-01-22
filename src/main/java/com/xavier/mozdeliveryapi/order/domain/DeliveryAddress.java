package com.xavier.mozdeliveryapi.order.domain;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a delivery address.
 */
public record DeliveryAddress(
    String street,
    String city,
    String district,
    String postalCode,
    String country,
    double latitude,
    double longitude,
    String deliveryInstructions
) implements ValueObject {
    
    public DeliveryAddress {
        Objects.requireNonNull(street, "Street cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(country, "Country cannot be null");
        
        if (street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be empty");
        }
        
        if (city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        
        if (country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }
        
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }
    
    public static DeliveryAddress of(String street, String city, String district, 
                                   String postalCode, String country, 
                                   double latitude, double longitude) {
        return new DeliveryAddress(street, city, district, postalCode, country, 
                                 latitude, longitude, null);
    }
    
    public static DeliveryAddress of(String street, String city, String district, 
                                   String postalCode, String country, 
                                   double latitude, double longitude, 
                                   String deliveryInstructions) {
        return new DeliveryAddress(street, city, district, postalCode, country, 
                                 latitude, longitude, deliveryInstructions);
    }
    
    /**
     * Get formatted address string.
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        
        if (district != null && !district.trim().isEmpty()) {
            sb.append(", ").append(district);
        }
        
        sb.append(", ").append(city);
        
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        
        sb.append(", ").append(country);
        
        return sb.toString();
    }
}