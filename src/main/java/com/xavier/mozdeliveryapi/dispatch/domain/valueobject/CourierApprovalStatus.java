package com.xavier.mozdeliveryapi.dispatch.domain.valueobject;

/**
 * Enumeration representing the approval status of a courier registration.
 */
public enum CourierApprovalStatus {
    
    /**
     * Registration submitted and pending review.
     */
    PENDING,
    
    /**
     * Registration approved and courier can start working.
     */
    APPROVED,
    
    /**
     * Registration rejected due to failed verification.
     */
    REJECTED,
    
    /**
     * Courier account suspended due to policy violations.
     */
    SUSPENDED,
    
    /**
     * Courier account terminated.
     */
    TERMINATED;
    
    /**
     * Check if this status allows the courier to work.
     */
    public boolean canWork() {
        return this == APPROVED;
    }
    
    /**
     * Check if this status represents an active courier.
     */
    public boolean isActive() {
        return this == APPROVED;
    }
    
    /**
     * Check if status can transition to the given new status.
     */
    public boolean canTransitionTo(CourierApprovalStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == APPROVED || newStatus == REJECTED;
            case APPROVED -> newStatus == SUSPENDED || newStatus == TERMINATED;
            case REJECTED -> newStatus == PENDING; // Can reapply
            case SUSPENDED -> newStatus == APPROVED || newStatus == TERMINATED;
            case TERMINATED -> false; // Terminal state
        };
    }
}