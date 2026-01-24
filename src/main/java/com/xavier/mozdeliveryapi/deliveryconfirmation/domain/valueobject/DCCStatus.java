package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject;

/**
 * Status of a Delivery Confirmation Code.
 */
public enum DCCStatus {
    /**
     * Code is active and can be used for validation.
     */
    ACTIVE,
    
    /**
     * Code has been successfully used for delivery confirmation.
     */
    USED,
    
    /**
     * Code has expired or exceeded maximum attempts.
     */
    EXPIRED
}