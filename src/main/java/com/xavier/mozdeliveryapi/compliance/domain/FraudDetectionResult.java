package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Result of fraud detection analysis.
 */
public record FraudDetectionResult(
    FraudRiskLevel riskLevel,
    double riskScore,
    List<String> riskFactors,
    Map<String, Object> analysisDetails,
    String recommendation
) {
    
    public FraudDetectionResult {
        Objects.requireNonNull(riskLevel, "Risk level cannot be null");
        if (riskScore < 0.0 || riskScore > 1.0) {
            throw new IllegalArgumentException("Risk score must be between 0.0 and 1.0");
        }
        Objects.requireNonNull(riskFactors, "Risk factors cannot be null");
        Objects.requireNonNull(analysisDetails, "Analysis details cannot be null");
        Objects.requireNonNull(recommendation, "Recommendation cannot be null");
    }
    
    /**
     * Check if the transaction should be allowed.
     */
    public boolean shouldAllowTransaction() {
        return !riskLevel.shouldBlockTransaction();
    }
    
    /**
     * Check if manual review is required.
     */
    public boolean requiresManualReview() {
        return riskLevel.requiresManualReview();
    }
    
    /**
     * Check if alerts should be triggered.
     */
    public boolean shouldTriggerAlert() {
        return riskLevel.shouldTriggerAlert();
    }
}