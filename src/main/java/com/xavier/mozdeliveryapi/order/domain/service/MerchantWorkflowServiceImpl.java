package com.xavier.mozdeliveryapi.order.domain.service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of MerchantWorkflowService.
 */
@Service
public class MerchantWorkflowServiceImpl implements MerchantWorkflowService {
    
    private final MerchantRepository merchantRepository;
    private final Map<MerchantId, MerchantWorkflowRules> rulesCache;
    
    public MerchantWorkflowServiceImpl(MerchantRepository merchantRepository) {
        this.merchantRepository = Objects.requireNonNull(merchantRepository, 
            "Merchant repository cannot be null");
        this.rulesCache = new ConcurrentHashMap<>();
    }
    
    @Override
    public MerchantWorkflowRules getWorkflowRules(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        // Check cache first
        MerchantWorkflowRules cachedRules = rulesCache.get(merchantId);
        if (cachedRules != null) {
            return cachedRules;
        }
        
        // Load merchant and create default rules
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
        
        MerchantWorkflowRules rules = MerchantWorkflowRules.defaultForVertical(
            merchantId, merchant.getVertical());
        
        // Cache the rules
        rulesCache.put(merchantId, rules);
        
        return rules;
    }
    
    @Override
    public void updateWorkflowRules(MerchantId merchantId, MerchantWorkflowRules rules) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(rules, "Workflow rules cannot be null");
        
        // Validate that the merchant exists
        if (!merchantRepository.findById(merchantId).isPresent()) {
            throw new IllegalArgumentException("Merchant not found: " + merchantId);
        }
        
        // Update cache
        rulesCache.put(merchantId, rules);
        
        // In a real implementation, this would also persist to database
    }
    
    @Override
    public void resetToDefaultRules(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        // Load merchant
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
        
        // Create default rules and update
        MerchantWorkflowRules defaultRules = MerchantWorkflowRules.defaultForVertical(
            merchantId, merchant.getVertical());
        
        updateWorkflowRules(merchantId, defaultRules);
    }
}