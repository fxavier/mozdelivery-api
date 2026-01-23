package com.xavier.mozdeliveryapi.compliance.domain.exception;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.ConsentId;

/**
 * Exception thrown when a consent is not found.
 */
public class ConsentNotFoundException extends RuntimeException {
    
    private final ConsentId consentId;
    
    public ConsentNotFoundException(ConsentId consentId) {
        super("Consent not found: " + consentId);
        this.consentId = consentId;
    }
    
    public ConsentId getConsentId() {
        return consentId;
    }
}