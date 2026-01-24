package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Exception thrown when a Delivery Confirmation Code is not found.
 */
public class DCCNotFoundException extends DCCException {
    
    public DCCNotFoundException(OrderId orderId) {
        super("Delivery confirmation code not found for order: " + orderId.value());
    }
    
    public DCCNotFoundException(String message) {
        super(message);
    }
    
    public DCCNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}