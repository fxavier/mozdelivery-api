package com.xavier.mozdeliveryapi.deliveryconfirmation.infra.web;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.AdminOverrideRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.AdminOverrideResult;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CourierLockoutClearRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DCCSecurityService;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DeliveryConfirmationApplicationService;

import jakarta.validation.Valid;

/**
 * REST controller for DCC admin operations.
 * Provides APIs for admin override capabilities with audit trails.
 */
@RestController
@RequestMapping("/api/v1/admin/delivery-confirmation")
@PreAuthorize("hasRole('ADMIN')")
public class DCCAdminController {
    
    private final DeliveryConfirmationApplicationService deliveryConfirmationService;
    
    public DCCAdminController(DeliveryConfirmationApplicationService deliveryConfirmationService) {
        this.deliveryConfirmationService = Objects.requireNonNull(deliveryConfirmationService,
            "Delivery confirmation service cannot be null");
    }
    
    /**
     * Perform admin override operations on delivery confirmation codes.
     * This endpoint allows admins to force expire codes or complete deliveries.
     * 
     * @param request The admin override request
     * @return The result of the override operation
     */
    @PostMapping("/override")
    public ResponseEntity<AdminOverrideResult> performAdminOverride(@Valid @RequestBody AdminOverrideRequest request) {
        AdminOverrideResult result = deliveryConfirmationService.performAdminOverride(request);
        
        if (result.successful()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Clear courier lockout.
     * This endpoint allows admins to clear security lockouts for couriers.
     * 
     * @param request The courier lockout clear request
     * @return The result of the operation
     */
    @PostMapping("/clear-lockout")
    public ResponseEntity<AdminOverrideResult> clearCourierLockout(@Valid @RequestBody CourierLockoutClearRequest request) {
        AdminOverrideResult result = deliveryConfirmationService.clearCourierLockout(request);
        
        if (result.successful()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Get courier validation statistics for security monitoring.
     * This endpoint provides insights into courier validation patterns.
     * 
     * @param courierId The courier ID
     * @param hoursBack Number of hours to look back (default: 24)
     * @return The validation statistics
     */
    @GetMapping("/courier-stats/{courierId}")
    public ResponseEntity<DCCSecurityService.ValidationStats> getCourierValidationStats(
            @PathVariable String courierId,
            @RequestParam(defaultValue = "24") int hoursBack) {
        
        try {
            Instant since = Instant.now().minus(hoursBack, ChronoUnit.HOURS);
            DCCSecurityService.ValidationStats stats = deliveryConfirmationService
                .getCourierValidationStats(courierId, since);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}