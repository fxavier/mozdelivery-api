package com.xavier.mozdeliveryapi.dispatch.infra.web;

import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierApprovalRequest;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.CourierRegistrationService;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.shared.infra.config.RequirePermission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * REST controller for courier administration operations.
 */
@RestController
@RequestMapping("/api/v1/admin/couriers")
@Tag(name = "Courier Administration", description = "Administrative operations for courier management")
public class CourierAdminController {
    
    private final CourierRegistrationService courierRegistrationService;
    
    public CourierAdminController(CourierRegistrationService courierRegistrationService) {
        this.courierRegistrationService = Objects.requireNonNull(courierRegistrationService, 
                                                                "Courier registration service cannot be null");
    }
    
    @GetMapping("/pending")
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Get pending registrations", description = "Get all pending courier registrations")
    public ResponseEntity<List<CourierProfile>> getPendingRegistrations() {
        List<CourierProfile> pendingCouriers = courierRegistrationService.getPendingRegistrations();
        return ResponseEntity.ok(pendingCouriers);
    }
    
    @GetMapping
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Get couriers by status", description = "Get couriers filtered by approval status")
    public ResponseEntity<List<CourierProfile>> getCouriersByStatus(
            @RequestParam(required = false) CourierApprovalStatus status) {
        
        List<CourierProfile> couriers;
        if (status != null) {
            couriers = courierRegistrationService.getCouriersByStatus(status);
        } else {
            // Return all approved couriers by default
            couriers = courierRegistrationService.getCouriersByStatus(CourierApprovalStatus.APPROVED);
        }
        
        return ResponseEntity.ok(couriers);
    }
    
    @GetMapping("/city/{city}")
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Get couriers by city", description = "Get couriers in a specific city")
    public ResponseEntity<List<CourierProfile>> getCouriersByCity(@PathVariable String city) {
        List<CourierProfile> couriers = courierRegistrationService.getCouriersByCity(city);
        return ResponseEntity.ok(couriers);
    }
    
    @PostMapping("/{deliveryPersonId}/approve")
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Process courier approval", description = "Approve or reject a courier registration")
    public ResponseEntity<CourierProfile> processApproval(
            @PathVariable DeliveryPersonId deliveryPersonId,
            @Valid @RequestBody CourierApprovalRequest request) {
        
        // Ensure path parameter matches request body
        if (!deliveryPersonId.equals(request.deliveryPersonId())) {
            return ResponseEntity.badRequest().build();
        }
        
        CourierProfile updatedProfile = courierRegistrationService.processApproval(request);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/{deliveryPersonId}/suspend")
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Suspend courier", description = "Suspend a courier account")
    public ResponseEntity<CourierProfile> suspendCourier(
            @PathVariable DeliveryPersonId deliveryPersonId,
            @RequestParam String reason) {
        
        CourierProfile updatedProfile = courierRegistrationService.suspendCourier(deliveryPersonId, reason);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/{deliveryPersonId}/reactivate")
    @RequirePermission(Permission.MANAGE_COURIERS)
    @Operation(summary = "Reactivate courier", description = "Reactivate a suspended courier account")
    public ResponseEntity<CourierProfile> reactivateCourier(@PathVariable DeliveryPersonId deliveryPersonId) {
        CourierProfile updatedProfile = courierRegistrationService.reactivateCourier(deliveryPersonId);
        return ResponseEntity.ok(updatedProfile);
    }
}