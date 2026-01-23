package com.xavier.mozdeliveryapi.notification.application.usecase;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * Service to resolve tenant information from various contexts.
 * This is a simplified implementation for MVP.
 */
@Service
public class TenantResolutionService {
    
    /**
     * Resolve tenant ID from order ID.
     * In a real implementation, this would query the order repository.
     */
    public TenantId resolveTenantFromOrderId(String orderId) {
        // For MVP, we'll use a simple mapping
        // In production, this would query the order repository
        return TenantId.of("tenant-" + orderId.hashCode() % 10);
    }
    
    /**
     * Resolve tenant ID from payment ID.
     * In a real implementation, this would query the payment repository.
     */
    public TenantId resolveTenantFromPaymentId(String paymentId) {
        // For MVP, we'll use a simple mapping
        // In production, this would query the payment repository
        return TenantId.of("tenant-" + paymentId.hashCode() % 10);
    }
    
    /**
     * Get default tenant for system events.
     */
    public TenantId getDefaultTenant() {
        return TenantId.of("default-tenant");
    }
}