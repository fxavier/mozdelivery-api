package com.xavier.mozdeliveryapi.order.domain;

/**
 * Enumeration of supported payment methods.
 */
public enum PaymentMethod {
    MPESA("M-Pesa", "Mobile money payment via M-Pesa"),
    MULTIBANCO("Multibanco", "Portuguese ATM network payment"),
    MB_WAY("MB Way", "Portuguese mobile payment system"),
    CREDIT_CARD("Credit Card", "International credit card payment"),
    DEBIT_CARD("Debit Card", "International debit card payment"),
    CASH_ON_DELIVERY("Cash on Delivery", "Pay with cash upon delivery");
    
    private final String displayName;
    private final String description;
    
    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this payment method requires online processing.
     */
    public boolean requiresOnlineProcessing() {
        return this != CASH_ON_DELIVERY;
    }
    
    /**
     * Check if this payment method supports refunds.
     */
    public boolean supportsRefunds() {
        return this != CASH_ON_DELIVERY;
    }
}