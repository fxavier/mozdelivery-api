package com.xavier.mozdeliveryapi.merchant.application.dto;

/**
 * Request DTO for merchant approval/rejection.
 */
public record MerchantApprovalRequest(
    String merchantId,
    boolean approved,
    String reason,
    String reviewedBy
) {
    
    public void validate() {
        if (merchantId == null || merchantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Merchant ID is required");
        }
        
        if (reviewedBy == null || reviewedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer information is required");
        }
        
        if (!approved && (reason == null || reason.trim().isEmpty())) {
            throw new IllegalArgumentException("Rejection reason is required when rejecting");
        }
    }
}