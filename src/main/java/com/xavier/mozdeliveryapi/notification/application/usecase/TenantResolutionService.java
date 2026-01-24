package com.xavier.mozdeliveryapi.notification.application.usecase;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Service to resolve merchant information from various contexts.
 * This is a simplified implementation for MVP.
 */
@Service
public class TenantResolutionService {
    
    /**
     * Resolve merchant ID from order ID.
     * In a real implementation, this would query the order repository.
     */
    public MerchantId resolveMerchantFromOrderId(String orderId) {
        // For MVP, we'll use a simple mapping
        // In production, this would query the order repository
        return MerchantId.of("merchant-" + orderId.hashCode() % 10);
    }
    
    /**
     * Resolve merchant ID from payment ID.
     * In a real implementation, this would query the payment repository.
     */
    public MerchantId resolveMerchantFromPaymentId(String paymentId) {
        // For MVP, we'll use a simple mapping
        // In production, this would query the payment repository
        return MerchantId.of("merchant-" + paymentId.hashCode() % 10);
    }
    
    /**
     * Get default merchant for system events.
     */
    public MerchantId getDefaultMerchant() {
        return MerchantId.of("default-merchant");
    }
}