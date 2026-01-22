package com.xavier.mozdeliveryapi.dispatch.domain;

/**
 * Exception thrown when a delivery assignment fails.
 */
public class DeliveryAssignmentException extends RuntimeException {
    
    public DeliveryAssignmentException(String message) {
        super(message);
    }
    
    public DeliveryAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}