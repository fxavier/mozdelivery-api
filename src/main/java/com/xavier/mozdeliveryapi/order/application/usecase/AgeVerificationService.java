package com.xavier.mozdeliveryapi.order.application.usecase;
import com.xavier.mozdeliveryapi.order.domain.valueobject.AgeVerification;
import com.xavier.mozdeliveryapi.order.domain.valueobject.RestrictedItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;

/**
 * Service for age verification operations.
 */
public interface AgeVerificationService {
    
    /**
     * Verify age using document information.
     */
    ValidationResult verifyAge(AgeVerification ageVerification);
    
    /**
     * Check if age verification is sufficient for restricted items.
     */
    ValidationResult validateAgeForRestrictedItems(AgeVerification ageVerification, 
                                                  java.util.List<RestrictedItem> restrictedItems);
    
    /**
     * Verify document authenticity.
     */
    boolean isDocumentAuthentic(String documentType, String documentNumber);
    
    /**
     * Check if person meets minimum age requirement.
     */
    boolean meetsMinimumAge(AgeVerification ageVerification, int minimumAge);
    
    /**
     * Create age verification from document scan.
     */
    AgeVerification createFromDocumentScan(String documentType, String documentNumber, 
                                         java.time.LocalDate dateOfBirth, String fullName);
}