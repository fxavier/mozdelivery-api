package com.xavier.mozdeliveryapi.shared.infra.web;

import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health check controller for testing the application setup.
 */
@RestController
@RequestMapping("/api/public")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", Instant.now(),
            "application", "mozdelivery-api",
            "version", "1.0.0"
        ));
    }
    
    @GetMapping("/tenant-test")
    public ResponseEntity<Map<String, Object>> tenantTest() {
        String currentTenant = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(Map.of(
            "currentTenant", currentTenant != null ? currentTenant : "No tenant set",
            "hasTenant", TenantContext.hasTenant(),
            "timestamp", Instant.now()
        ));
    }
}