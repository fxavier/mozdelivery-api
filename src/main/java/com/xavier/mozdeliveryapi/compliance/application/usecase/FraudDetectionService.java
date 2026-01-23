package com.xavier.mozdeliveryapi.compliance.application.usecase;

import java.util.Map;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.FraudDetectionResult;

/**
 * Domain service for fraud detection and risk assessment.
 */
public interface FraudDetectionService {
    
    /**
     * Analyze transaction for fraud risk.
     */
    FraudDetectionResult analyzeTransaction(String userId, TenantId tenantId, 
                                          String transactionType, Map<String, Object> transactionData);
    
    /**
     * Analyze user behavior for fraud risk.
     */
    FraudDetectionResult analyzeUserBehavior(String userId, TenantId tenantId, 
                                           Map<String, Object> behaviorData);
    
    /**
     * Analyze login attempt for fraud risk.
     */
    FraudDetectionResult analyzeLoginAttempt(String userId, String ipAddress, 
                                           String userAgent, Map<String, Object> loginData);
    
    /**
     * Update fraud detection rules and thresholds.
     */
    void updateFraudRules(Map<String, Object> rules);
    
    /**
     * Report confirmed fraud case for machine learning improvement.
     */
    void reportConfirmedFraud(String userId, TenantId tenantId, String transactionId, 
                             Map<String, Object> fraudDetails);
    
    /**
     * Report false positive for machine learning improvement.
     */
    void reportFalsePositive(String userId, TenantId tenantId, String transactionId, 
                           Map<String, Object> details);
}