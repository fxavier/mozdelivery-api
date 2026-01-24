package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of DCC generation service using secure random number generation.
 */
@Service
public class DCCGenerationServiceImpl implements DCCGenerationService {
    
    private static final Duration DEFAULT_EXPIRATION = Duration.ofHours(24);
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final int CODE_LENGTH = 4;
    
    private final SecureRandom secureRandom;
    
    public DCCGenerationServiceImpl() {
        this.secureRandom = new SecureRandom();
    }
    
    @Override
    public DeliveryConfirmationCode generateCode(OrderId orderId, Duration expirationDuration, int maxAttempts) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(expirationDuration, "Expiration duration cannot be null");
        
        validateExpirationDuration(expirationDuration);
        validateMaxAttempts(maxAttempts);
        
        String code = generateSecureCode();
        Instant expiresAt = Instant.now().plus(expirationDuration);
        
        return new DeliveryConfirmationCode(orderId, code, expiresAt, maxAttempts);
    }
    
    @Override
    public DeliveryConfirmationCode generateCode(OrderId orderId) {
        return generateCode(orderId, DEFAULT_EXPIRATION, DEFAULT_MAX_ATTEMPTS);
    }
    
    @Override
    public DeliveryConfirmationCode regenerateCode(OrderId orderId, Duration expirationDuration, int maxAttempts) {
        // For regeneration, we simply generate a new code with fresh settings
        return generateCode(orderId, expirationDuration, maxAttempts);
    }
    
    /**
     * Generate a secure 4-digit code using cryptographically secure random number generator.
     */
    private String generateSecureCode() {
        // Generate a number between 0000 and 9999
        int code = secureRandom.nextInt(10000);
        
        // Format as 4-digit string with leading zeros if necessary
        return String.format("%04d", code);
    }
    
    private void validateExpirationDuration(Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Expiration duration must be positive");
        }
        
        // Reasonable limits: minimum 1 minute, maximum 7 days
        if (duration.compareTo(Duration.ofMinutes(1)) < 0) {
            throw new IllegalArgumentException("Expiration duration must be at least 1 minute");
        }
        
        if (duration.compareTo(Duration.ofDays(7)) > 0) {
            throw new IllegalArgumentException("Expiration duration cannot exceed 7 days");
        }
    }
    
    private void validateMaxAttempts(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Max attempts must be at least 1");
        }
        
        if (maxAttempts > 10) {
            throw new IllegalArgumentException("Max attempts cannot exceed 10");
        }
    }
}