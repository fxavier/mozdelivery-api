package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

/**
 * Exception thrown when an invalid code is provided for DCC validation.
 */
public class DCCInvalidCodeException extends DCCException {
    
    public DCCInvalidCodeException(String message) {
        super(message);
    }
    
    public DCCInvalidCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}