package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

/**
 * Base exception for all Delivery Confirmation Code related exceptions.
 */
public abstract class DCCException extends RuntimeException {
    
    protected DCCException(String message) {
        super(message);
    }
    
    protected DCCException(String message, Throwable cause) {
        super(message, cause);
    }
}