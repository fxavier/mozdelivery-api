package com.xavier.mozdeliveryapi.payment.domain;

import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing the result of refund eligibility check.
 */
public record RefundEligibilityResult(
    boolean eligible,
    String reason,
    List<String> violations
) implements ValueObject {
    
    public RefundEligibilityResult {
        Objects.requireNonNull(violations, "Violations cannot be null");
        violations = List.copyOf(violations); // Defensive copy
    }
    
    public static RefundEligibilityResult success() {
        return new RefundEligibilityResult(true, "Refund is eligible", List.of());
    }
    
    public static RefundEligibilityResult notEligible(String reason, List<String> violations) {
        return new RefundEligibilityResult(false, reason, violations);
    }
    
    public static RefundEligibilityResult notEligible(String reason) {
        return new RefundEligibilityResult(false, reason, List.of(reason));
    }
}