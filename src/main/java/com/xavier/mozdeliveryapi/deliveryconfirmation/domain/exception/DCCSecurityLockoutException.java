package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

/**
 * Exception thrown when a courier is locked out due to security violations.
 */
public class DCCSecurityLockoutException extends DCCException {
    
    private final String courierId;
    private final long lockoutRemainingSeconds;
    
    public DCCSecurityLockoutException(String message, String courierId, long lockoutRemainingSeconds) {
        super(message);
        this.courierId = courierId;
        this.lockoutRemainingSeconds = lockoutRemainingSeconds;
    }
    
    public DCCSecurityLockoutException(String message, Throwable cause, String courierId, long lockoutRemainingSeconds) {
        super(message, cause);
        this.courierId = courierId;
        this.lockoutRemainingSeconds = lockoutRemainingSeconds;
    }
    
    public String getCourierId() {
        return courierId;
    }
    
    public long getLockoutRemainingSeconds() {
        return lockoutRemainingSeconds;
    }
}