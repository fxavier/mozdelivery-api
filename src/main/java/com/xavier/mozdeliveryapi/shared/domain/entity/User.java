package com.xavier.mozdeliveryapi.shared.domain.entity;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;

import java.time.Instant;
import java.util.Objects;

/**
 * User entity representing a platform user with role-based access.
 */
public class User extends AggregateRoot<UserId> {
    
    private final UserId id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private MerchantId merchantId; // null for non-merchant users
    private boolean active;
    private boolean emailVerified;
    private boolean phoneVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    
    // Constructor for new users
    public User(UserId id, String email, String firstName, String lastName, 
                String phoneNumber, UserRole role, MerchantId merchantId) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = validateEmail(email);
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
        this.phoneNumber = phoneNumber;
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.merchantId = merchantId;
        this.active = true;
        this.emailVerified = false;
        this.phoneVerified = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Constructor for existing users (from persistence)
    public User(UserId id, String email, String firstName, String lastName, 
                String phoneNumber, UserRole role, MerchantId merchantId,
                boolean active, boolean emailVerified, boolean phoneVerified,
                Instant createdAt, Instant updatedAt, Instant lastLoginAt) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.merchantId = merchantId;
        this.active = active;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
    }
    
    @Override
    protected UserId getId() {
        return id;
    }
    
    /**
     * Update user profile information.
     */
    public void updateProfile(String firstName, String lastName, String phoneNumber) {
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
        this.phoneNumber = phoneNumber;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Change user role (admin operation).
     */
    public void changeRole(UserRole newRole, MerchantId newMerchantId) {
        this.role = Objects.requireNonNull(newRole, "Role cannot be null");
        this.merchantId = newMerchantId;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Activate user account.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Deactivate user account.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Mark email as verified.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Mark phone as verified.
     */
    public void verifyPhone() {
        this.phoneVerified = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Record user login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }
    
    /**
     * Check if user belongs to a specific merchant.
     */
    public boolean belongsToMerchant(MerchantId merchantId) {
        return this.merchantId != null && this.merchantId.equals(merchantId);
    }
    
    /**
     * Check if user can access merchant resources.
     */
    public boolean canAccessMerchant(MerchantId merchantId) {
        return role == UserRole.ADMIN || belongsToMerchant(merchantId);
    }
    
    /**
     * Get full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.trim().toLowerCase();
    }
    
    private String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return name.trim();
    }
    
    // Getters
    public UserId getUserId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public UserRole getRole() { return role; }
    public MerchantId getMerchantId() { return merchantId; }
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public boolean isPhoneVerified() { return phoneVerified; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getLastLoginAt() { return lastLoginAt; }
}