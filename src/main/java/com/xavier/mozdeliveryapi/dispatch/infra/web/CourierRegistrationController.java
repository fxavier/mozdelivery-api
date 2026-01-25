package com.xavier.mozdeliveryapi.dispatch.infra.web;

import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateAvailabilityRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateVehicleInfoRequest;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.CourierRegistrationService;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.shared.infra.config.RequirePermission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * REST controller for courier registration and profile management.
 */
@RestController
@RequestMapping("/api/v1/couriers")
@Tag(name = "Courier Registration", description = "Courier registration and profile management operations")
public class CourierRegistrationController {
    
    private final CourierRegistrationService courierRegistrationService;
    
    public CourierRegistrationController(CourierRegistrationService courierRegistrationService) {
        this.courierRegistrationService = Objects.requireNonNull(courierRegistrationService, 
                                                                "Courier registration service cannot be null");
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register as a courier", description = "Submit courier registration application")
    public ResponseEntity<CourierRegistrationResponse> registerCourier(
            @Valid @RequestBody CourierRegistrationRequest request) {
        
        CourierRegistrationResponse response = courierRegistrationService.registerCourier(request);
        
        if (response.userId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{deliveryPersonId}")
    @RequirePermission(Permission.READ_COURIER_PROFILE)
    @Operation(summary = "Get courier profile", description = "Get courier profile by ID")
    public ResponseEntity<CourierProfile> getCourierProfile(
            @PathVariable DeliveryPersonId deliveryPersonId) {
        
        CourierProfile profile = courierRegistrationService.getCourierProfile(deliveryPersonId);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/{deliveryPersonId}/vehicle")
    @RequirePermission(Permission.UPDATE_COURIER_PROFILE)
    @Operation(summary = "Update vehicle information", description = "Update courier vehicle and capacity information")
    public ResponseEntity<CourierProfile> updateVehicleInfo(
            @PathVariable DeliveryPersonId deliveryPersonId,
            @Valid @RequestBody UpdateVehicleInfoRequest request) {
        
        // Ensure path parameter matches request body
        if (!deliveryPersonId.equals(request.deliveryPersonId())) {
            return ResponseEntity.badRequest().build();
        }
        
        CourierProfile updatedProfile = courierRegistrationService.updateVehicleInfo(request);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/{deliveryPersonId}/availability")
    @RequirePermission(Permission.UPDATE_COURIER_PROFILE)
    @Operation(summary = "Update availability schedule", description = "Update courier availability schedule")
    public ResponseEntity<CourierProfile> updateAvailability(
            @PathVariable DeliveryPersonId deliveryPersonId,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        
        // Ensure path parameter matches request body
        if (!deliveryPersonId.equals(request.deliveryPersonId())) {
            return ResponseEntity.badRequest().build();
        }
        
        CourierProfile updatedProfile = courierRegistrationService.updateAvailability(request);
        return ResponseEntity.ok(updatedProfile);
    }
}