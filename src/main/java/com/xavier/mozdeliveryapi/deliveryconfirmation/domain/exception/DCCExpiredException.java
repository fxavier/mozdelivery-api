package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

/**
 * Exception thrown when attempting to use an expired Delivery Confirmation Code.
 */
public class DCCExpiredException extends DCCException {
    
    public DCCExpiredException(String message) {
        super(message);
    }
    
    public DCCExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}