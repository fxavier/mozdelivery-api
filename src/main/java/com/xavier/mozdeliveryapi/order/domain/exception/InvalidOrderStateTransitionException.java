package com.xavier.mozdeliveryapi.order.domain.exception;

/**
 * Exception thrown when an invalid order state transition is attempted.
 */
public class InvalidOrderStateTransitionException extends RuntimeException {
    
    public InvalidOrderStateTransitionException(String message) {
        super(message);
    }
    
    public InvalidOrderStateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}