package com.xavier.mozdeliveryapi.order.domain.valueobject;


/**
 * Enumeration of possible order statuses.
 */
public enum OrderStatus {
    PENDING("Order created and awaiting payment"),
    PAYMENT_PROCESSING("Payment is being processed"),
    PAYMENT_CONFIRMED("Payment confirmed, order being prepared"),
    PREPARING("Order is being prepared by the business"),
    READY_FOR_PICKUP("Order is ready for delivery pickup"),
    OUT_FOR_DELIVERY("Order is out for delivery"),
    DELIVERED("Order has been delivered successfully"),
    CANCELLED("Order has been cancelled"),
    REFUNDED("Order has been refunded");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the order can be cancelled from this status.
     */
    public boolean canBeCancelled() {
        return switch (this) {
            case PENDING, PAYMENT_PROCESSING, PAYMENT_CONFIRMED, PREPARING -> true;
            case READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED -> false;
        };
    }
    
    /**
     * Check if the order can transition to the given status.
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PAYMENT_PROCESSING || newStatus == PAYMENT_CONFIRMED || newStatus == CANCELLED;
            case PAYMENT_PROCESSING -> newStatus == PAYMENT_CONFIRMED || newStatus == CANCELLED;
            case PAYMENT_CONFIRMED -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY_FOR_PICKUP || newStatus == CANCELLED;
            case READY_FOR_PICKUP -> newStatus == OUT_FOR_DELIVERY || newStatus == CANCELLED;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED || newStatus == CANCELLED; // Allow cancellation during delivery
            case DELIVERED -> newStatus == REFUNDED;
            case CANCELLED -> newStatus == REFUNDED;
            case REFUNDED -> false; // Terminal state
        };
    }
    
    /**
     * Get all valid next statuses from current status.
     */
    public java.util.Set<OrderStatus> getValidNextStatuses() {
        return switch (this) {
            case PENDING -> java.util.Set.of(PAYMENT_PROCESSING, PAYMENT_CONFIRMED, CANCELLED);
            case PAYMENT_PROCESSING -> java.util.Set.of(PAYMENT_CONFIRMED, CANCELLED);
            case PAYMENT_CONFIRMED -> java.util.Set.of(PREPARING, CANCELLED);
            case PREPARING -> java.util.Set.of(READY_FOR_PICKUP, CANCELLED);
            case READY_FOR_PICKUP -> java.util.Set.of(OUT_FOR_DELIVERY, CANCELLED);
            case OUT_FOR_DELIVERY -> java.util.Set.of(DELIVERED, CANCELLED);
            case DELIVERED -> java.util.Set.of(REFUNDED);
            case CANCELLED -> java.util.Set.of(REFUNDED);
            case REFUNDED -> java.util.Set.of(); // Terminal state
        };
    }
    
    /**
     * Check if this status requires merchant action.
     */
    public boolean requiresMerchantAction() {
        return switch (this) {
            case PAYMENT_CONFIRMED, PREPARING -> true;
            case PENDING, PAYMENT_PROCESSING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, 
                 DELIVERED, CANCELLED, REFUNDED -> false;
        };
    }
    
    /**
     * Check if this status requires courier action.
     */
    public boolean requiresCourierAction() {
        return switch (this) {
            case READY_FOR_PICKUP, OUT_FOR_DELIVERY -> true;
            case PENDING, PAYMENT_PROCESSING, PAYMENT_CONFIRMED, PREPARING, 
                 DELIVERED, CANCELLED, REFUNDED -> false;
        };
    }
    
    /**
     * Check if refund is allowed from this status.
     */
    public boolean allowsRefund() {
        return switch (this) {
            case CANCELLED, DELIVERED -> true;
            case PENDING, PAYMENT_PROCESSING, PAYMENT_CONFIRMED, PREPARING, 
                 READY_FOR_PICKUP, OUT_FOR_DELIVERY, REFUNDED -> false;
        };
    }
    
    /**
     * Check if this is a terminal status (no further transitions allowed).
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == REFUNDED;
    }
    
    /**
     * Check if the order is active (not cancelled or refunded).
     */
    public boolean isActive() {
        return this != CANCELLED && this != REFUNDED;
    }
}