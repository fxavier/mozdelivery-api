package com.xavier.mozdeliveryapi.order.domain.valueobject;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;

/**
 * Enumeration of order cancellation reasons.
 */
public enum CancellationReason {
    CUSTOMER_REQUEST("Customer requested cancellation"),
    PAYMENT_FAILED("Payment processing failed"),
    OUT_OF_STOCK("Items are out of stock"),
    BUSINESS_CLOSED("Business is closed or unavailable"),
    DELIVERY_UNAVAILABLE("Delivery is not available to the address"),
    SYSTEM_ERROR("System error occurred"),
    FRAUD_DETECTED("Fraudulent activity detected"),
    COMPLIANCE_VIOLATION("Compliance requirements not met");
    
    private final String description;
    
    CancellationReason(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this cancellation reason allows for automatic refund.
     */
    public boolean allowsAutomaticRefund() {
        return switch (this) {
            case PAYMENT_FAILED, OUT_OF_STOCK, BUSINESS_CLOSED, 
                 DELIVERY_UNAVAILABLE, SYSTEM_ERROR -> true;
            case CUSTOMER_REQUEST, FRAUD_DETECTED, COMPLIANCE_VIOLATION -> false;
        };
    }
}