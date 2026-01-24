package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCAttempt;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event.DCCGeneratedEvent;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event.DCCValidatedEvent;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event.DCCExpiredEvent;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.event.DCCValidationFailedEvent;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCExpiredException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCMaxAttemptsExceededException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCInvalidCodeException;

/**
 * Delivery Confirmation Code aggregate root.
 * 
 * Manages the lifecycle of delivery confirmation codes including generation,
 * validation, expiration, and attempt tracking for secure delivery completion.
 */
public class DeliveryConfirmationCode extends AggregateRoot<OrderId> {
    
    private final OrderId orderId;
    private final String code;
    private DCCStatus status;
    private final Instant generatedAt;
    private final Instant expiresAt;
    private final int maxAttempts;
    private int attemptCount;
    private final List<DCCAttempt> attempts;
    
    /**
     * Create a new delivery confirmation code.
     */
    public DeliveryConfirmationCode(OrderId orderId, String code, Instant expiresAt, int maxAttempts) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.code = Objects.requireNonNull(code, "Code cannot be null");
        this.status = DCCStatus.ACTIVE;
        this.generatedAt = Instant.now();
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration time cannot be null");
        this.maxAttempts = validateMaxAttempts(maxAttempts);
        this.attemptCount = 0;
        this.attempts = new ArrayList<>();
        
        validateCode(code);
        validateExpirationTime(expiresAt);
        
        // Register domain event
        registerEvent(DCCGeneratedEvent.of(orderId, code, generatedAt, expiresAt));
    }
    
    /**
     * Constructor for reconstituting from persistence.
     */
    public DeliveryConfirmationCode(OrderId orderId, String code, DCCStatus status,
                                  Instant generatedAt, Instant expiresAt, int maxAttempts,
                                  int attemptCount, List<DCCAttempt> attempts) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.code = Objects.requireNonNull(code, "Code cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.generatedAt = Objects.requireNonNull(generatedAt, "Generated at cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration time cannot be null");
        this.maxAttempts = validateMaxAttempts(maxAttempts);
        this.attemptCount = Math.max(0, attemptCount);
        this.attempts = attempts != null ? new ArrayList<>(attempts) : new ArrayList<>();
    }
    
    @Override
    protected OrderId getId() {
        return orderId;
    }
    
    /**
     * Validate the provided code against this DCC.
     * 
     * @param providedCode The code to validate
     * @param courierId The courier attempting validation
     * @return true if validation successful
     * @throws DCCExpiredException if the code has expired
     * @throws DCCMaxAttemptsExceededException if max attempts exceeded
     * @throws DCCInvalidCodeException if the code is invalid
     */
    public boolean validate(String providedCode, String courierId) {
        Objects.requireNonNull(providedCode, "Provided code cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        // Check if already used or expired
        if (status == DCCStatus.USED) {
            throw new IllegalStateException("DCC has already been used");
        }
        
        if (status == DCCStatus.EXPIRED) {
            throw new DCCExpiredException("DCC has expired");
        }
        
        // Check expiration
        if (isExpired()) {
            expire();
            throw new DCCExpiredException("DCC has expired");
        }
        
        // Check max attempts
        if (attemptCount >= maxAttempts) {
            expire();
            throw new DCCMaxAttemptsExceededException("Maximum validation attempts exceeded");
        }
        
        // Record the attempt
        DCCAttempt attempt = new DCCAttempt(courierId, providedCode, Instant.now());
        attempts.add(attempt);
        attemptCount++;
        
        // Validate the code
        boolean isValid = code.equals(providedCode);
        
        if (isValid) {
            // Mark as used
            status = DCCStatus.USED;
            registerEvent(DCCValidatedEvent.of(orderId, courierId, Instant.now()));
            return true;
        } else {
            // Register failed attempt
            registerEvent(DCCValidationFailedEvent.of(orderId, courierId, providedCode, attemptCount, maxAttempts));
            
            // Check if this was the last attempt
            if (attemptCount >= maxAttempts) {
                expire();
                throw new DCCMaxAttemptsExceededException("Maximum validation attempts exceeded");
            }
            
            throw new DCCInvalidCodeException("Invalid confirmation code");
        }
    }
    
    /**
     * Check if the code has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the code is still active (not used or expired).
     */
    public boolean isActive() {
        return status == DCCStatus.ACTIVE && !isExpired();
    }
    
    /**
     * Check if the code has been used.
     */
    public boolean isUsed() {
        return status == DCCStatus.USED;
    }
    
    /**
     * Get remaining attempts.
     */
    public int getRemainingAttempts() {
        return Math.max(0, maxAttempts - attemptCount);
    }
    
    /**
     * Expire the code.
     */
    public void expire() {
        if (status != DCCStatus.EXPIRED) {
            status = DCCStatus.EXPIRED;
            registerEvent(DCCExpiredEvent.of(orderId, Instant.now()));
        }
    }
    
    /**
     * Force expire the code (admin action).
     */
    public void forceExpire(String adminId, String reason) {
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        status = DCCStatus.EXPIRED;
        registerEvent(DCCExpiredEvent.ofForced(orderId, adminId, reason, Instant.now()));
    }
    
    private void validateCode(String code) {
        if (code.length() != 4) {
            throw new IllegalArgumentException("DCC must be exactly 4 digits");
        }
        
        if (!code.matches("\\d{4}")) {
            throw new IllegalArgumentException("DCC must contain only digits");
        }
    }
    
    private void validateExpirationTime(Instant expiresAt) {
        if (expiresAt.isBefore(generatedAt)) {
            throw new IllegalArgumentException("Expiration time cannot be before generation time");
        }
        
        if (expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration time cannot be in the past");
        }
    }
    
    private int validateMaxAttempts(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be at least 1");
        }
        
        if (maxAttempts > 10) {
            throw new IllegalArgumentException("Max attempts cannot exceed 10");
        }
        
        return maxAttempts;
    }
    
    // Getters
    public OrderId getOrderId() { return orderId; }
    public String getCode() { return code; }
    public DCCStatus getStatus() { return status; }
    public Instant getGeneratedAt() { return generatedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public int getMaxAttempts() { return maxAttempts; }
    public int getAttemptCount() { return attemptCount; }
    public List<DCCAttempt> getAttempts() { return Collections.unmodifiableList(attempts); }
}