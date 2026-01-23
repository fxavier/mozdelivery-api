package com.xavier.mozdeliveryapi.order.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Result of a validation operation.
 */
public record ValidationResult(
    boolean isValid,
    List<ValidationError> errors
) implements ValueObject {
    
    public ValidationResult {
        Objects.requireNonNull(errors, "Errors list cannot be null");
        errors = new ArrayList<>(errors); // Defensive copy
    }
    
    public static ValidationResult valid() {
        return new ValidationResult(true, Collections.emptyList());
    }
    
    public static ValidationResult invalid(List<ValidationError> errors) {
        return new ValidationResult(false, errors);
    }
    
    public static ValidationResult invalid(ValidationError error) {
        return new ValidationResult(false, List.of(error));
    }
    
    public static ValidationResult invalid(String errorCode, String message) {
        return invalid(new ValidationError(errorCode, message));
    }
    
    /**
     * Combine this result with another validation result.
     */
    public ValidationResult combine(ValidationResult other) {
        Objects.requireNonNull(other, "Other validation result cannot be null");
        
        if (this.isValid && other.isValid) {
            return valid();
        }
        
        List<ValidationError> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(other.errors);
        
        return invalid(combinedErrors);
    }
    
    /**
     * Get the first error message, if any.
     */
    public String getFirstErrorMessage() {
        return errors.isEmpty() ? null : errors.get(0).message();
    }
    
    public static record ValidationError(
        String errorCode,
        String message
    ) {
        public ValidationError {
            Objects.requireNonNull(errorCode, "Error code cannot be null");
            Objects.requireNonNull(message, "Message cannot be null");
            
            if (errorCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Error code cannot be empty");
            }
            
            if (message.trim().isEmpty()) {
                throw new IllegalArgumentException("Message cannot be empty");
            }
        }
    }
}