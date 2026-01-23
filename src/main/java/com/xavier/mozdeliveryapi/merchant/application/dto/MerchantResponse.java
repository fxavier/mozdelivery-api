package com.xavier.mozdeliveryapi.merchant.application.dto;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Response DTO for merchant information.
 */
public record MerchantResponse(
    String merchantId,
    String businessName,
    String displayName,
    String contactEmail,
    String contactPhone,
    String businessAddress,
    String city,
    String country,
    Vertical vertical,
    MerchantStatus status,
    boolean canProcessOrders,
    boolean isPubliclyVisible,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(
            merchant.getMerchantId().toString(),
            merchant.getBusinessName(),
            merchant.getDisplayName(),
            merchant.getBusinessDetails().contactEmail(),
            merchant.getBusinessDetails().contactPhone(),
            merchant.getBusinessDetails().businessAddress(),
            merchant.getBusinessDetails().city(),
            merchant.getBusinessDetails().country(),
            merchant.getVertical(),
            merchant.getStatus(),
            merchant.canProcessOrders(),
            merchant.isPubliclyVisible(),
            merchant.getCreatedAt(),
            merchant.getUpdatedAt()
        );
    }
    
    /**
     * Create a public response with limited information for guest browsing.
     */
    public static MerchantResponse publicFrom(Merchant merchant) {
        return new MerchantResponse(
            merchant.getMerchantId().toString(),
            merchant.getBusinessName(),
            merchant.getDisplayName(),
            null, // Hide contact email in public view
            null, // Hide contact phone in public view
            merchant.getBusinessDetails().city(), // Show only city, not full address
            merchant.getBusinessDetails().city(),
            merchant.getBusinessDetails().country(),
            merchant.getVertical(),
            merchant.getStatus(),
            merchant.canProcessOrders(),
            merchant.isPubliclyVisible(),
            merchant.getCreatedAt(),
            merchant.getUpdatedAt()
        );
    }
}