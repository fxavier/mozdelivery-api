package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

import java.time.Instant;

/**
 * Value object representing merchant approval status and history.
 */
public record ApprovalStatus(
    MerchantStatus status,
    String reason,
    String approvedBy,
    Instant approvedAt,
    String rejectionReason
) {
    
    public static ApprovalStatus pending() {
        return new ApprovalStatus(MerchantStatus.PENDING, "Application submitted", null, null, null);
    }
    
    public static ApprovalStatus approved(String approvedBy) {
        return new ApprovalStatus(MerchantStatus.ACTIVE, "Application approved", approvedBy, Instant.now(), null);
    }
    
    public static ApprovalStatus rejected(String rejectionReason, String rejectedBy) {
        return new ApprovalStatus(MerchantStatus.REJECTED, "Application rejected", rejectedBy, Instant.now(), rejectionReason);
    }
    
    public boolean isPending() {
        return status == MerchantStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status.isApproved();
    }
    
    public boolean isRejected() {
        return status == MerchantStatus.REJECTED;
    }
}