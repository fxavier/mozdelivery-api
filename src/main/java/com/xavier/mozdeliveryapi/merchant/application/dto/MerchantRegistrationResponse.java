package com.xavier.mozdeliveryapi.merchant.application.dto;

import java.time.Instant;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Response DTO for merchant registration.
 */
public record MerchantRegistrationResponse(
    String merchantId,
    String businessName,
    String displayName,
    String contactEmail,
    String city,
    Vertical vertical,
    MerchantStatus status,
    String message,
    Instant registeredAt
) {
    
    public static MerchantRegistrationResponse success(
            String merchantId, 
            String businessName, 
            String displayName,
            String contactEmail,
            String city,
            Vertical vertical,
            Instant registeredAt) {
        return new MerchantRegistrationResponse(
            merchantId,
            businessName,
            displayName,
            contactEmail,
            city,
            vertical,
            MerchantStatus.PENDING,
            "Merchant registration submitted successfully. Your application is pending approval.",
            registeredAt
        );
    }
}