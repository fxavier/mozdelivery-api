package com.xavier.mozdeliveryapi.order.domain.service;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Service for managing merchant-specific workflow rules and configurations.
 */
public interface MerchantWorkflowService {
    
    /**
     * Get workflow rules for a specific merchant.
     */
    MerchantWorkflowRules getWorkflowRules(MerchantId merchantId);
    
    /**
     * Update workflow rules for a merchant.
     */
    void updateWorkflowRules(MerchantId merchantId, MerchantWorkflowRules rules);
    
    /**
     * Reset workflow rules to default for a merchant's vertical.
     */
    void resetToDefaultRules(MerchantId merchantId);
}