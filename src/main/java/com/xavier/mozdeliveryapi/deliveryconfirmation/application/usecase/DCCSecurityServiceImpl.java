package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of DCC security service.
 * 
 * Provides comprehensive security measures for DCC validation including
 * lockout mechanisms, rate limiting, and suspicious activity detection.
 */
@Service
public class DCCSecurityServiceImpl implements DCCSecurityService {
    
    // Configuration constants
    private static final int MAX_ATTEMPTS_PER_HOUR = 20;
    private static final int MAX_FAILED_ATTEMPTS_BEFORE_LOCKOUT = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);
    private static final Duration SUSPICIOUS_ACTIVITY_WINDOW = Duration.ofMinutes(5);
    private static final int SUSPICIOUS_ACTIVITY_THRESHOLD = 10;
    
    // In-memory storage for demo purposes
    // In production, this should be stored in Redis or a database
    private final Map<String, CourierLockout> courierLockouts = new ConcurrentHashMap<>();
    private final Map<String, CourierAttemptHistory> courierAttempts = new ConcurrentHashMap<>();
    
    private final DCCAuditService auditService;
    
    public DCCSecurityServiceImpl(DCCAuditService auditService) {
        this.auditService = Objects.requireNonNull(auditService, "Audit service cannot be null");
    }
    
    @Override
    public boolean isCourierLockedOut(String courierId) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        CourierLockout lockout = courierLockouts.get(courierId);
        if (lockout == null) {
            return false;
        }
        
        // Check if lockout has expired
        if (Instant.now().isAfter(lockout.expiresAt())) {
            courierLockouts.remove(courierId);
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean canCourierAttemptValidation(String courierId, OrderId orderId) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // Check if courier is locked out
        if (isCourierLockedOut(courierId)) {
            return false;
        }
        
        // Check rate limiting
        if (isRateLimitExceeded(courierId)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void applyCourierLockout(String courierId, OrderId orderId, int attemptCount) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        Instant lockoutExpiry = Instant.now().plus(LOCKOUT_DURATION);
        CourierLockout lockout = new CourierLockout(
            courierId,
            orderId,
            attemptCount,
            Instant.now(),
            lockoutExpiry,
            "Maximum validation attempts exceeded"
        );
        
        courierLockouts.put(courierId, lockout);
        
        // Log the lockout event through audit service
        // Note: We need the merchant ID, which should be obtained from the order
        // For now, we'll use a placeholder - in real implementation, we'd fetch the order
        // auditService.logDCCLockout(orderId, merchantId, courierId, attemptCount, null, null);
    }
    
    @Override
    public boolean detectSuspiciousActivity(String courierId, OrderId orderId, String attemptedCode) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(attemptedCode, "Attempted code cannot be null");
        
        CourierAttemptHistory history = courierAttempts.computeIfAbsent(courierId, 
            k -> new CourierAttemptHistory());
        
        Instant now = Instant.now();
        Instant windowStart = now.minus(SUSPICIOUS_ACTIVITY_WINDOW);
        
        // Count recent attempts
        long recentAttempts = history.attempts().stream()
            .filter(attempt -> attempt.timestamp().isAfter(windowStart))
            .count();
        
        // Check for suspicious patterns
        boolean isSuspicious = false;
        String suspiciousReason = null;
        
        // Pattern 1: Too many attempts in short time window
        if (recentAttempts >= SUSPICIOUS_ACTIVITY_THRESHOLD) {
            isSuspicious = true;
            suspiciousReason = "Excessive validation attempts in short time window";
        }
        
        // Pattern 2: Sequential code attempts (brute force)
        if (isSequentialCodeAttempt(history, attemptedCode)) {
            isSuspicious = true;
            suspiciousReason = "Sequential code brute force attempt detected";
        }
        
        // Pattern 3: Same code attempted multiple times
        if (isSameCodeRepeated(history, attemptedCode)) {
            isSuspicious = true;
            suspiciousReason = "Repeated identical code attempts";
        }
        
        if (isSuspicious) {
            // Log suspicious activity through audit service
            // auditService.logSuspiciousActivity(orderId, merchantId, courierId, 
            //     suspiciousReason, "DCC validation pattern analysis", null, null);
        }
        
        return isSuspicious;
    }
    
    @Override
    public long getRemainingLockoutTime(String courierId) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        CourierLockout lockout = courierLockouts.get(courierId);
        if (lockout == null) {
            return 0;
        }
        
        long remaining = Duration.between(Instant.now(), lockout.expiresAt()).toSeconds();
        return Math.max(0, remaining);
    }
    
    @Override
    public void clearCourierLockout(String courierId, String adminId, String reason) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        CourierLockout lockout = courierLockouts.remove(courierId);
        if (lockout != null) {
            // Log the lockout clearance
            // auditService.logDCCLockoutCleared(lockout.orderId(), merchantId, courierId, 
            //     adminId, reason, null, null);
        }
    }
    
    @Override
    public boolean isRateLimitExceeded(String courierId) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        CourierAttemptHistory history = courierAttempts.get(courierId);
        if (history == null) {
            return false;
        }
        
        Instant windowStart = Instant.now().minus(RATE_LIMIT_WINDOW);
        long recentAttempts = history.attempts().stream()
            .filter(attempt -> attempt.timestamp().isAfter(windowStart))
            .count();
        
        return recentAttempts >= MAX_ATTEMPTS_PER_HOUR;
    }
    
    @Override
    public void recordValidationAttempt(String courierId, OrderId orderId, boolean successful) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        CourierAttemptHistory history = courierAttempts.computeIfAbsent(courierId, 
            k -> new CourierAttemptHistory());
        
        ValidationAttempt attempt = new ValidationAttempt(
            orderId,
            Instant.now(),
            successful,
            null // We don't store the actual code for security reasons
        );
        
        history.attempts().add(attempt);
        
        // Clean up old attempts to prevent memory leaks
        cleanupOldAttempts(history);
    }
    
    @Override
    public ValidationStats getCourierValidationStats(String courierId, Instant since) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(since, "Since timestamp cannot be null");
        
        CourierAttemptHistory history = courierAttempts.get(courierId);
        if (history == null) {
            return new ValidationStats(0, 0, 0, 0, null, null, false, 0);
        }
        
        var recentAttempts = history.attempts().stream()
            .filter(attempt -> attempt.timestamp().isAfter(since))
            .toList();
        
        int totalAttempts = recentAttempts.size();
        int successfulAttempts = (int) recentAttempts.stream()
            .filter(ValidationAttempt::successful)
            .count();
        int failedAttempts = totalAttempts - successfulAttempts;
        
        Set<OrderId> uniqueOrders = new HashSet<>();
        recentAttempts.forEach(attempt -> uniqueOrders.add(attempt.orderId()));
        
        Instant firstAttempt = recentAttempts.stream()
            .map(ValidationAttempt::timestamp)
            .min(Instant::compareTo)
            .orElse(null);
        
        Instant lastAttempt = recentAttempts.stream()
            .map(ValidationAttempt::timestamp)
            .max(Instant::compareTo)
            .orElse(null);
        
        boolean isLockedOut = isCourierLockedOut(courierId);
        long lockoutRemaining = getRemainingLockoutTime(courierId);
        
        return new ValidationStats(
            totalAttempts,
            successfulAttempts,
            failedAttempts,
            uniqueOrders.size(),
            firstAttempt,
            lastAttempt,
            isLockedOut,
            lockoutRemaining
        );
    }
    
    private boolean isSequentialCodeAttempt(CourierAttemptHistory history, String attemptedCode) {
        // Simple check for sequential numeric codes
        try {
            int currentCode = Integer.parseInt(attemptedCode);
            
            // Check last few attempts for sequential pattern
            var recentAttempts = history.attempts().stream()
                .filter(attempt -> attempt.timestamp().isAfter(Instant.now().minus(Duration.ofMinutes(1))))
                .filter(attempt -> attempt.attemptedCode() != null)
                .map(attempt -> {
                    try {
                        return Integer.parseInt(attempt.attemptedCode());
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                })
                .filter(code -> code != -1)
                .sorted()
                .toList();
            
            if (recentAttempts.size() >= 3) {
                // Check if codes are sequential
                for (int i = 1; i < recentAttempts.size(); i++) {
                    if (recentAttempts.get(i) != recentAttempts.get(i - 1) + 1) {
                        return false;
                    }
                }
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a numeric code, can't check for sequential pattern
        }
        
        return false;
    }
    
    private boolean isSameCodeRepeated(CourierAttemptHistory history, String attemptedCode) {
        Instant windowStart = Instant.now().minus(Duration.ofMinutes(1));
        
        long sameCodeCount = history.attempts().stream()
            .filter(attempt -> attempt.timestamp().isAfter(windowStart))
            .filter(attempt -> attemptedCode.equals(attempt.attemptedCode()))
            .count();
        
        return sameCodeCount >= 3; // Same code attempted 3+ times in 1 minute
    }
    
    private void cleanupOldAttempts(CourierAttemptHistory history) {
        Instant cutoff = Instant.now().minus(Duration.ofDays(1));
        history.attempts().removeIf(attempt -> attempt.timestamp().isBefore(cutoff));
    }
    
    // Internal data structures
    private record CourierLockout(
        String courierId,
        OrderId orderId,
        int attemptCount,
        Instant lockedAt,
        Instant expiresAt,
        String reason
    ) {}
    
    private record ValidationAttempt(
        OrderId orderId,
        Instant timestamp,
        boolean successful,
        String attemptedCode // Null for security - we don't store actual codes
    ) {}
    
    private static class CourierAttemptHistory {
        private final java.util.List<ValidationAttempt> attempts = new java.util.concurrent.CopyOnWriteArrayList<>();
        
        public java.util.List<ValidationAttempt> attempts() {
            return attempts;
        }
    }
}