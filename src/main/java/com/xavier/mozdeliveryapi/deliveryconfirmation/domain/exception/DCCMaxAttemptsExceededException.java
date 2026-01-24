package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

/**
 * Exception thrown when maximum validation attempts for a DCC have been exceeded.
 */
public class DCCMaxAttemptsExceededException extends DCCException {
    
    public DCCMaxAttemptsExceededException(String message) {
        super(message);
    }
    
    public DCCMaxAttemptsExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}