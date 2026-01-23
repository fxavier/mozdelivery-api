package com.xavier.mozdeliveryapi.tenant.application.dto;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;

import java.time.Instant;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Response object for tenant onboarding.
 */
public record TenantOnboardingResponse(
    TenantId tenantId,
    String tenantName,
    Vertical vertical,
    String status,
    Instant createdAt,
    String message
) {
    
    public static TenantOnboardingResponse success(TenantId tenantId, String tenantName, 
                                                  Vertical vertical, Instant createdAt) {
        return new TenantOnboardingResponse(
            tenantId,
            tenantName,
            vertical,
            "SUCCESS",
            createdAt,
            "Tenant successfully onboarded"
        );
    }
    
    public static TenantOnboardingResponse failure(String message) {
        return new TenantOnboardingResponse(
            null,
            null,
            null,
            "FAILURE",
            null,
            message
        );
    }
}