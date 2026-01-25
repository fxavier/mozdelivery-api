package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.AvailabilitySchedule;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.VehicleInfo;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request to register a new courier.
 */
public record CourierRegistrationRequest(
    @NotBlank(message = "First name is required")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    String lastName,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,
    
    @NotNull(message = "Vehicle information is required")
    VehicleInfo vehicleInfo,
    
    @NotNull(message = "Delivery capacity is required")
    DeliveryCapacity deliveryCapacity,
    
    @NotNull(message = "Initial location is required")
    Location initialLocation,
    
    @NotNull(message = "Availability schedule is required")
    AvailabilitySchedule availabilitySchedule,
    
    @NotBlank(message = "City is required")
    String city,
    
    String drivingLicenseNumber,
    
    String emergencyContactName,
    
    String emergencyContactPhone,
    
    String notes
) {
    
    public CourierRegistrationRequest {
        if (firstName != null && firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName != null && lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (email != null && email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (phoneNumber != null && phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        if (city != null && city.isBlank()) {
            throw new IllegalArgumentException("City cannot be blank");
        }
    }
    
    /**
     * Get full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}