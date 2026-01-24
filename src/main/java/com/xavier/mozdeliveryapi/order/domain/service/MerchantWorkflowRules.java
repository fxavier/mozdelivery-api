package com.xavier.mozdeliveryapi.order.domain.service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Merchant-specific workflow rules and configurations.
 */
public record MerchantWorkflowRules(
    MerchantId merchantId,
    Vertical vertical,
    boolean autoAcceptOrders,
    boolean requiresPreparationConfirmation,
    boolean allowsCancellationDuringDelivery,
    Duration preparationTimeLimit,
    Duration pickupTimeLimit,
    Duration deliveryTimeLimit,
    Set<OrderStatus> merchantControlledStatuses,
    Map<OrderStatus, Duration> statusTimeouts,
    boolean supportsPartialRefunds,
    boolean requiresDeliveryConfirmation
) {
    
    /**
     * Create default workflow rules for a vertical.
     */
    public static MerchantWorkflowRules defaultForVertical(MerchantId merchantId, Vertical vertical) {
        return switch (vertical) {
            case RESTAURANT -> new MerchantWorkflowRules(
                merchantId,
                vertical,
                false, // Manual order acceptance for restaurants
                true,  // Requires preparation confirmation
                false, // No cancellation during delivery for food
                Duration.ofMinutes(30), // 30 min preparation
                Duration.ofMinutes(10), // 10 min pickup window
                Duration.ofMinutes(45), // 45 min delivery
                Set.of(OrderStatus.PAYMENT_CONFIRMED, OrderStatus.PREPARING),
                Map.of(
                    OrderStatus.PAYMENT_CONFIRMED, Duration.ofMinutes(5),
                    OrderStatus.PREPARING, Duration.ofMinutes(30),
                    OrderStatus.READY_FOR_PICKUP, Duration.ofMinutes(10),
                    OrderStatus.OUT_FOR_DELIVERY, Duration.ofMinutes(45)
                ),
                false, // No partial refunds for food
                true   // Requires delivery confirmation
            );
            
            case GROCERY -> new MerchantWorkflowRules(
                merchantId,
                vertical,
                true,  // Auto-accept grocery orders
                true,  // Requires preparation confirmation
                true,  // Allow cancellation during delivery
                Duration.ofMinutes(60), // 60 min preparation
                Duration.ofMinutes(15), // 15 min pickup window
                Duration.ofMinutes(60), // 60 min delivery
                Set.of(OrderStatus.PREPARING),
                Map.of(
                    OrderStatus.PAYMENT_CONFIRMED, Duration.ofMinutes(2),
                    OrderStatus.PREPARING, Duration.ofMinutes(60),
                    OrderStatus.READY_FOR_PICKUP, Duration.ofMinutes(15),
                    OrderStatus.OUT_FOR_DELIVERY, Duration.ofMinutes(60)
                ),
                true,  // Supports partial refunds
                true   // Requires delivery confirmation
            );
            
            case PHARMACY -> new MerchantWorkflowRules(
                merchantId,
                vertical,
                false, // Manual acceptance for prescription validation
                true,  // Requires preparation confirmation
                false, // No cancellation during delivery for medicines
                Duration.ofMinutes(45), // 45 min preparation
                Duration.ofMinutes(5),  // 5 min pickup window
                Duration.ofMinutes(30), // 30 min delivery
                Set.of(OrderStatus.PAYMENT_CONFIRMED, OrderStatus.PREPARING),
                Map.of(
                    OrderStatus.PAYMENT_CONFIRMED, Duration.ofMinutes(10), // Time for prescription validation
                    OrderStatus.PREPARING, Duration.ofMinutes(45),
                    OrderStatus.READY_FOR_PICKUP, Duration.ofMinutes(5),
                    OrderStatus.OUT_FOR_DELIVERY, Duration.ofMinutes(30)
                ),
                false, // No partial refunds for medicines
                true   // Requires delivery confirmation
            );
            
            default -> new MerchantWorkflowRules(
                merchantId,
                vertical,
                true,  // Auto-accept by default
                false, // No preparation confirmation required
                true,  // Allow cancellation during delivery
                Duration.ofMinutes(30), // 30 min preparation
                Duration.ofMinutes(10), // 10 min pickup window
                Duration.ofMinutes(45), // 45 min delivery
                Set.of(OrderStatus.PREPARING),
                Map.of(
                    OrderStatus.PAYMENT_CONFIRMED, Duration.ofMinutes(5),
                    OrderStatus.PREPARING, Duration.ofMinutes(30),
                    OrderStatus.READY_FOR_PICKUP, Duration.ofMinutes(10),
                    OrderStatus.OUT_FOR_DELIVERY, Duration.ofMinutes(45)
                ),
                true,  // Supports partial refunds
                false  // No delivery confirmation required
            );
        };
    }
    
    /**
     * Check if merchant controls the given status.
     */
    public boolean controlsStatus(OrderStatus status) {
        return merchantControlledStatuses.contains(status);
    }
    
    /**
     * Get timeout for a specific status.
     */
    public Duration getTimeoutForStatus(OrderStatus status) {
        return statusTimeouts.getOrDefault(status, Duration.ofHours(24)); // Default 24h timeout
    }
    
    /**
     * Check if status requires merchant confirmation.
     */
    public boolean requiresConfirmation(OrderStatus status) {
        return switch (status) {
            case PAYMENT_CONFIRMED -> !autoAcceptOrders;
            case PREPARING -> requiresPreparationConfirmation;
            default -> false;
        };
    }
}