package com.xavier.mozdeliveryapi.order.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;


/**
 * Value object representing age verification information.
 */
public record AgeVerification(
    String documentType,
    String documentNumber,
    LocalDate dateOfBirth,
    String fullName,
    boolean verified
) implements ValueObject {
    
    public AgeVerification {
        Objects.requireNonNull(documentType, "Document type cannot be null");
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        Objects.requireNonNull(fullName, "Full name cannot be null");
        
        if (documentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Document type cannot be empty");
        }
        
        if (documentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Document number cannot be empty");
        }
        
        if (fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
    }
    
    /**
     * Calculate the current age.
     */
    public int getCurrentAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    /**
     * Check if the person is at least the specified age.
     */
    public boolean isAtLeastAge(int minimumAge) {
        return getCurrentAge() >= minimumAge;
    }
    
    /**
     * Check if the person is an adult (18 or older).
     */
    public boolean isAdult() {
        return isAtLeastAge(18);
    }
    
    /**
     * Create an unverified age verification record.
     */
    public static AgeVerification unverified(String documentType, String documentNumber, 
                                           LocalDate dateOfBirth, String fullName) {
        return new AgeVerification(documentType, documentNumber, dateOfBirth, fullName, false);
    }
    
    /**
     * Create a verified age verification record.
     */
    public static AgeVerification verified(String documentType, String documentNumber, 
                                         LocalDate dateOfBirth, String fullName) {
        return new AgeVerification(documentType, documentNumber, dateOfBirth, fullName, true);
    }
    
    /**
     * Mark this verification as verified.
     */
    public AgeVerification markAsVerified() {
        return new AgeVerification(documentType, documentNumber, dateOfBirth, fullName, true);
    }
}