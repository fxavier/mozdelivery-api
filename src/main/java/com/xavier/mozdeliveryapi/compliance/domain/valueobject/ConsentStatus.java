package com.xavier.mozdeliveryapi.compliance.domain.valueobject;

/**
 * Status of consent given by a data subject.
 */
public enum ConsentStatus {
    /**
     * Consent has been given.
     */
    GIVEN,
    
    /**
     * Consent has been withdrawn.
     */
    WITHDRAWN,
    
    /**
     * Consent has expired and needs renewal.
     */
    EXPIRED,
    
    /**
     * Consent is pending (requested but not yet given).
     */
    PENDING;
    
    /**
     * Check if consent is currently valid and active.
     */
    public boolean isActive() {
        return this == GIVEN;
    }
    
    /**
     * Check if consent allows data processing.
     */
    public boolean allowsProcessing() {
        return this == GIVEN;
    }
}