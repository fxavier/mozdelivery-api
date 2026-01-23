package com.xavier.mozdeliveryapi.shared.domain.valueobject;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;

/**
 * Enumeration of refund reasons.
 */
public enum RefundReason {
    ORDER_CANCELLED("Order was cancelled"),
    PAYMENT_FAILED("Payment processing failed"),
    ITEM_UNAVAILABLE("Ordered items are unavailable"),
    DELIVERY_FAILED("Delivery could not be completed"),
    CUSTOMER_REQUEST("Customer requested refund"),
    QUALITY_ISSUE("Quality issue with delivered items"),
    SYSTEM_ERROR("System error occurred");
    
    private final String description;
    
    RefundReason(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this refund reason allows for automatic processing.
     */
    public boolean allowsAutomaticProcessing() {
        return switch (this) {
            case ORDER_CANCELLED, PAYMENT_FAILED, ITEM_UNAVAILABLE, SYSTEM_ERROR -> true;
            case DELIVERY_FAILED, CUSTOMER_REQUEST, QUALITY_ISSUE -> false;
        };
    }
}