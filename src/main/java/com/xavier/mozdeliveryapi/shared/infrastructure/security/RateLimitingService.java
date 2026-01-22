package com.xavier.mozdeliveryapi.shared.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for implementing rate limiting using token bucket algorithm.
 */
@Service
public class RateLimitingService {
    
    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    
    /**
     * Check if request is allowed based on rate limiting rules.
     * 
     * @param key the rate limiting key (e.g., tenant ID, user ID, IP address)
     * @param requestsPerMinute maximum requests allowed per minute
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String key, int requestsPerMinute) {
        Bucket bucket = getBucket(key, requestsPerMinute);
        return bucket.tryConsume(1);
    }
    
    /**
     * Check if request is allowed with custom time window.
     * 
     * @param key the rate limiting key
     * @param requests maximum requests allowed
     * @param duration time window duration
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String key, int requests, Duration duration) {
        Bucket bucket = getBucket(key, requests, duration);
        return bucket.tryConsume(1);
    }
    
    /**
     * Get remaining tokens for a key.
     * 
     * @param key the rate limiting key
     * @param requestsPerMinute maximum requests allowed per minute
     * @return number of remaining tokens
     */
    public long getRemainingTokens(String key, int requestsPerMinute) {
        Bucket bucket = getBucket(key, requestsPerMinute);
        return bucket.getAvailableTokens();
    }
    
    /**
     * Get time until next token refill.
     * 
     * @param key the rate limiting key
     * @param requestsPerMinute maximum requests allowed per minute
     * @return duration until next refill in seconds
     */
    public long getSecondsUntilRefill(String key, int requestsPerMinute) {
        Bucket bucket = getBucket(key, requestsPerMinute);
        return bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill() / 1_000_000_000;
    }
    
    private Bucket getBucket(String key, int requestsPerMinute) {
        return getBucket(key, requestsPerMinute, Duration.ofMinutes(1));
    }
    
    private Bucket getBucket(String key, int requests, Duration duration) {
        return bucketCache.computeIfAbsent(key, k -> createBucket(requests, duration));
    }
    
    private Bucket createBucket(int requests, Duration duration) {
        Bandwidth bandwidth = Bandwidth.classic(requests, Refill.intervally(requests, duration));
        return Bucket.builder()
            .addLimit(bandwidth)
            .build();
    }
    
    /**
     * Clear rate limiting data for a key.
     * 
     * @param key the rate limiting key to clear
     */
    public void clearRateLimit(String key) {
        bucketCache.remove(key);
    }
    
    /**
     * Clear all rate limiting data.
     */
    public void clearAllRateLimits() {
        bucketCache.clear();
    }
}
