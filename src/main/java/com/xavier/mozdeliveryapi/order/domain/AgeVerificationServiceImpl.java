package com.xavier.mozdeliveryapi.order.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

/**
 * Implementation of AgeVerificationService.
 */
@Service
public class AgeVerificationServiceImpl implements AgeVerificationService {
    
    @Override
    public ValidationResult verifyAge(AgeVerification ageVerification) {
        Objects.requireNonNull(ageVerification, "Age verification cannot be null");
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        
        // Check if document is authentic
        if (!isDocumentAuthentic(ageVerification.documentType(), ageVerification.documentNumber())) {
            errors.add(new ValidationResult.ValidationError(
                "INVALID_DOCUMENT",
                "Document is not authentic or valid"
            ));
        }
        
        // Check if date of birth is reasonable
        if (ageVerification.dateOfBirth().isAfter(LocalDate.now())) {
            errors.add(new ValidationResult.ValidationError(
                "INVALID_DATE_OF_BIRTH",
                "Date of birth cannot be in the future"
            ));
        }
        
        // Check if person is too old (over 120 years)
        if (ageVerification.getCurrentAge() > 120) {
            errors.add(new ValidationResult.ValidationError(
                "UNREALISTIC_AGE",
                "Age appears to be unrealistic"
            ));
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validateAgeForRestrictedItems(AgeVerification ageVerification, 
                                                         List<RestrictedItem> restrictedItems) {
        Objects.requireNonNull(ageVerification, "Age verification cannot be null");
        Objects.requireNonNull(restrictedItems, "Restricted items cannot be null");
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        
        for (RestrictedItem item : restrictedItems) {
            if (item.requiresAgeVerification()) {
                if (!meetsMinimumAge(ageVerification, item.minimumAge())) {
                    errors.add(new ValidationResult.ValidationError(
                        "AGE_REQUIREMENT_NOT_MET",
                        String.format("Customer age (%d) does not meet minimum requirement (%d) for %s",
                                    ageVerification.getCurrentAge(), item.minimumAge(), item.productName())
                    ));
                }
            }
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public boolean isDocumentAuthentic(String documentType, String documentNumber) {
        Objects.requireNonNull(documentType, "Document type cannot be null");
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        
        // For demonstration, consider documents with proper format as authentic
        // In real implementation, this would integrate with government databases
        return switch (documentType.toUpperCase()) {
            case "ID_CARD", "PASSPORT", "DRIVER_LICENSE" -> 
                documentNumber.trim().length() >= 6 && !documentNumber.trim().isEmpty();
            default -> false;
        };
    }
    
    @Override
    public boolean meetsMinimumAge(AgeVerification ageVerification, int minimumAge) {
        Objects.requireNonNull(ageVerification, "Age verification cannot be null");
        
        return ageVerification.getCurrentAge() >= minimumAge;
    }
    
    @Override
    public AgeVerification createFromDocumentScan(String documentType, String documentNumber, 
                                                 LocalDate dateOfBirth, String fullName) {
        Objects.requireNonNull(documentType, "Document type cannot be null");
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        Objects.requireNonNull(fullName, "Full name cannot be null");
        
        // Verify the document and mark as verified if authentic
        boolean isAuthentic = isDocumentAuthentic(documentType, documentNumber);
        
        return new AgeVerification(documentType, documentNumber, dateOfBirth, fullName, isAuthentic);
    }
}