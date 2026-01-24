package com.xavier.mozdeliveryapi.order.domain.valueobject;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Secure tracking token for guest orders.
 */
public record GuestTrackingToken(
    String token,
    Instant generatedAt,
    Instant expiresAt
) implements ValueObject {
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32; // 256 bits
    private static final long EXPIRY_HOURS = 72; // 3 days
    
    public GuestTrackingToken {
        Objects.requireNonNull(token, "Token cannot be null");
        Objects.requireNonNull(generatedAt, "Generated at cannot be null");
        Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        
        if (token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        
        if (expiresAt.isBefore(generatedAt)) {
            throw new IllegalArgumentException("Expiry time cannot be before generation time");
        }
    }
    
    /**
     * Generate a new secure tracking token.
     */
    public static GuestTrackingToken generate() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        
        String token = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(tokenBytes);
        
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(EXPIRY_HOURS * 3600);
        
        return new GuestTrackingToken(token, now, expiry);
    }
    
    /**
     * Create a tracking token from existing values (for persistence).
     */
    public static GuestTrackingToken of(String token, Instant generatedAt, Instant expiresAt) {
        return new GuestTrackingToken(token, generatedAt, expiresAt);
    }
    
    /**
     * Check if the token is expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the token is valid (not expired).
     */
    public boolean isValid() {
        return !isExpired();
    }
    
    /**
     * Get the token value.
     */
    public String getValue() {
        return token;
    }
    
    /**
     * Get remaining validity time in seconds.
     */
    public long getRemainingValiditySeconds() {
        if (isExpired()) {
            return 0;
        }
        return expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
    }
    
    @Override
    public String toString() {
        return "GuestTrackingToken{" +
            "token=***" + token.substring(Math.max(0, token.length() - 4)) +
            ", generatedAt=" + generatedAt +
            ", expiresAt=" + expiresAt +
            ", isValid=" + isValid() +
            '}';
    }
}