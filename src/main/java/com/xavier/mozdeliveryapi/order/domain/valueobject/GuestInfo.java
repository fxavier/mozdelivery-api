package com.xavier.mozdeliveryapi.order.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Guest information for orders placed without registration.
 */
public record GuestInfo(
    String contactPhone,
    String contactEmail,
    String contactName,
    GuestTrackingToken trackingToken,
    Instant createdAt
) implements ValueObject {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // E.164 format
    );
    
    public GuestInfo {
        Objects.requireNonNull(contactPhone, "Contact phone cannot be null");
        Objects.requireNonNull(contactEmail, "Contact email cannot be null");
        Objects.requireNonNull(contactName, "Contact name cannot be null");
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        Objects.requireNonNull(createdAt, "Created at cannot be null");
        
        validatePhone(contactPhone);
        validateEmail(contactEmail);
        validateName(contactName);
    }
    
    public static GuestInfo create(String contactPhone, String contactEmail, String contactName) {
        return new GuestInfo(
            contactPhone,
            contactEmail,
            contactName,
            GuestTrackingToken.generate(),
            Instant.now()
        );
    }
    
    private void validatePhone(String phone) {
        if (phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone cannot be empty");
        }
        
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
    
    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    private void validateName(String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact name cannot be empty");
        }
        
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Contact name must be at least 2 characters");
        }
        
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Contact name cannot exceed 100 characters");
        }
    }
    
    /**
     * Get normalized phone number.
     */
    public String getNormalizedPhone() {
        return contactPhone.trim();
    }
    
    /**
     * Get normalized email.
     */
    public String getNormalizedEmail() {
        return contactEmail.trim().toLowerCase();
    }
    
    /**
     * Get normalized name.
     */
    public String getNormalizedName() {
        return contactName.trim();
    }
}