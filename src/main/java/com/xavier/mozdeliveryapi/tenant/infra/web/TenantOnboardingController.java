package com.xavier.mozdeliveryapi.tenant.infra.web;

import com.xavier.mozdeliveryapi.tenant.application.dto.TenantOnboardingRequest;
import com.xavier.mozdeliveryapi.tenant.application.dto.TenantOnboardingResponse;
import com.xavier.mozdeliveryapi.tenant.application.usecase.TenantOnboardingService;
import com.xavier.mozdeliveryapi.tenant.application.usecase.TenantOnboardingStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * REST controller for tenant onboarding operations.
 */
@RestController
@RequestMapping("/api/v1/tenants")
@CrossOrigin(origins = "*")
public class TenantOnboardingController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantOnboardingController.class);
    
    private final TenantOnboardingService onboardingService;
    
    public TenantOnboardingController(TenantOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }
    
    /**
     * Onboard a new tenant.
     */
    @PostMapping("/onboard")
    public ResponseEntity<TenantOnboardingResponse> onboardTenant(
            @Valid @RequestBody TenantOnboardingRequest request) {
        
        logger.info("Received tenant onboarding request for: {}", request.tenantName());
        
        try {
            TenantOnboardingResponse response = onboardingService.onboardTenant(request);
            
            if ("SUCCESS".equals(response.status())) {
                logger.info("Tenant onboarding successful for: {}", request.tenantName());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                logger.warn("Tenant onboarding failed for: {} - {}", request.tenantName(), response.message());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during tenant onboarding for: {}", request.tenantName(), e);
            TenantOnboardingResponse errorResponse = TenantOnboardingResponse.failure(
                "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get tenant onboarding statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<TenantOnboardingStats> getOnboardingStats() {
        logger.info("Retrieving tenant onboarding statistics");
        
        try {
            TenantOnboardingStats stats = onboardingService.getOnboardingStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving tenant onboarding statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check endpoint for tenant service.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tenant service is healthy");
    }
}