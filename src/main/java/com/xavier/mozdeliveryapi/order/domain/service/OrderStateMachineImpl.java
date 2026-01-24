package com.xavier.mozdeliveryapi.order.domain.service;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.exception.InvalidOrderStateTransitionException;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of OrderStateMachine with comprehensive business rules.
 */
@Service
public class OrderStateMachineImpl implements OrderStateMachine {
    
    private final MerchantWorkflowService merchantWorkflowService;
    
    public OrderStateMachineImpl(MerchantWorkflowService merchantWorkflowService) {
        this.merchantWorkflowService = Objects.requireNonNull(merchantWorkflowService, 
            "Merchant workflow service cannot be null");
    }
    
    @Override
    public boolean canTransition(Order order, OrderStatus targetStatus) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(targetStatus, "Target status cannot be null");
        
        // Check basic state machine rules
        if (!order.getStatus().canTransitionTo(targetStatus)) {
            return false;
        }
        
        // Check merchant-specific rules
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        // Special validation for merchant-controlled statuses
        if (rules.controlsStatus(targetStatus) && rules.requiresConfirmation(targetStatus)) {
            // This transition requires explicit merchant confirmation
            return true; // Allow but will require confirmation in executeTransition
        }
        
        // Validate business rules for specific transitions
        return validateTransitionBusinessRules(order, targetStatus, rules);
    }
    
    @Override
    public Set<OrderStatus> getValidNextStatuses(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        Set<OrderStatus> basicNextStatuses = order.getStatus().getValidNextStatuses();
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        // Filter based on merchant-specific rules
        return basicNextStatuses.stream()
            .filter(status -> validateTransitionBusinessRules(order, status, rules))
            .collect(java.util.stream.Collectors.toSet());
    }
    
    @Override
    public void executeTransition(Order order, OrderStatus targetStatus) {
        executeTransition(order, targetStatus, null);
    }
    
    @Override
    public void executeTransition(Order order, OrderStatus targetStatus, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(targetStatus, "Target status cannot be null");
        
        if (!canTransition(order, targetStatus)) {
            throw new InvalidOrderStateTransitionException(
                String.format("Cannot transition order %s from %s to %s", 
                    order.getOrderId(), order.getStatus(), targetStatus));
        }
        
        // Execute pre-transition business logic
        executePreTransitionLogic(order, targetStatus);
        
        // Execute the transition
        order.updateStatus(targetStatus);
        
        // Execute post-transition business logic
        executePostTransitionLogic(order, targetStatus, reason);
    }
    
    @Override
    public boolean canCancel(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (!order.canBeCancelled()) {
            return false;
        }
        
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        // Check if cancellation is allowed during delivery for this merchant
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            return rules.allowsCancellationDuringDelivery();
        }
        
        return true;
    }
    
    @Override
    public void cancelOrder(Order order, CancellationReason reason, String details) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
        
        if (!canCancel(order)) {
            throw new InvalidOrderStateTransitionException(
                String.format("Cannot cancel order %s in status %s", 
                    order.getOrderId(), order.getStatus()));
        }
        
        // Execute pre-cancellation logic
        executeCancellationLogic(order, reason);
        
        // Cancel the order
        order.cancel(reason, details);
        
        // Execute post-cancellation logic (e.g., trigger refund if applicable)
        executePostCancellationLogic(order, reason);
    }
    
    @Override
    public boolean canRefund(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // Check if order status allows refund
        if (!order.getStatus().allowsRefund()) {
            return false;
        }
        
        // Check if payment method supports refunds
        if (!order.getPaymentInfo().method().supportsRefunds()) {
            return false;
        }
        
        // Check if already refunded
        return order.getStatus() != OrderStatus.REFUNDED;
    }
    
    @Override
    public void processRefund(Order order, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (!canRefund(order)) {
            throw new InvalidOrderStateTransitionException(
                String.format("Cannot refund order %s in status %s", 
                    order.getOrderId(), order.getStatus()));
        }
        
        // Execute refund logic (this would integrate with payment service)
        executeRefundLogic(order, reason);
        
        // Update order status to refunded
        order.updateStatus(OrderStatus.REFUNDED);
    }
    
    @Override
    public MerchantWorkflowRules getMerchantWorkflowRules(MerchantId merchantId) {
        return merchantWorkflowService.getWorkflowRules(merchantId);
    }
    
    @Override
    public boolean canAutoProgress(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        return switch (order.getStatus()) {
            case PENDING -> {
                // Auto-progress if cash on delivery or if merchant auto-accepts
                yield order.getPaymentInfo().method() == PaymentMethod.CASH_ON_DELIVERY ||
                      rules.autoAcceptOrders();
            }
            case PAYMENT_CONFIRMED -> rules.autoAcceptOrders();
            default -> false;
        };
    }
    
    @Override
    public OrderStatus getNextAutoStatus(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (!canAutoProgress(order)) {
            throw new IllegalStateException("Order cannot auto-progress from current status");
        }
        
        return switch (order.getStatus()) {
            case PENDING -> {
                if (order.getPaymentInfo().method() == PaymentMethod.CASH_ON_DELIVERY) {
                    yield OrderStatus.PAYMENT_CONFIRMED;
                } else {
                    yield OrderStatus.PAYMENT_PROCESSING;
                }
            }
            case PAYMENT_CONFIRMED -> OrderStatus.PREPARING;
            default -> throw new IllegalStateException("No auto-progression available for status: " + order.getStatus());
        };
    }
    
    @Override
    public void executeAutoProgression(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (canAutoProgress(order)) {
            OrderStatus nextStatus = getNextAutoStatus(order);
            executeTransition(order, nextStatus, "Auto-progression");
        }
    }
    
    @Override
    public void validateBusinessRules(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        // Validate timeout rules
        validateStatusTimeout(order, rules);
        
        // Validate merchant-specific rules
        validateMerchantSpecificRules(order, rules);
    }
    
    @Override
    public boolean requiresManualIntervention(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        
        // Check if current status requires merchant confirmation
        if (rules.requiresConfirmation(order.getStatus())) {
            return true;
        }
        
        // Check if order has been in current status too long
        Duration timeInStatus = Duration.between(order.getUpdatedAt(), Instant.now());
        Duration timeout = rules.getTimeoutForStatus(order.getStatus());
        
        return timeInStatus.compareTo(timeout) > 0;
    }
    
    @Override
    public long getStatusTimeoutMinutes(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
        return rules.getTimeoutForStatus(order.getStatus()).toMinutes();
    }
    
    @Override
    public void handleStatusTimeout(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // Handle timeout based on current status
        switch (order.getStatus()) {
            case PAYMENT_PROCESSING -> {
                // Cancel order if payment processing takes too long
                cancelOrder(order, CancellationReason.PAYMENT_FAILED, "Payment processing timeout");
            }
            case PAYMENT_CONFIRMED -> {
                // Cancel order if merchant doesn't accept in time
                cancelOrder(order, CancellationReason.BUSINESS_CLOSED, "Merchant acceptance timeout");
            }
            case PREPARING -> {
                // Cancel order if preparation takes too long
                cancelOrder(order, CancellationReason.SYSTEM_ERROR, "Preparation timeout");
            }
            case READY_FOR_PICKUP -> {
                // Cancel order if not picked up in time
                cancelOrder(order, CancellationReason.DELIVERY_UNAVAILABLE, "Pickup timeout");
            }
            case OUT_FOR_DELIVERY -> {
                // Mark as delivery failed if delivery takes too long
                cancelOrder(order, CancellationReason.DELIVERY_UNAVAILABLE, "Delivery timeout");
            }
            default -> {
                // No timeout handling for other statuses
            }
        }
    }
    
    // Private helper methods
    
    private boolean validateTransitionBusinessRules(Order order, OrderStatus targetStatus, MerchantWorkflowRules rules) {
        // Add specific business rule validations here
        return switch (targetStatus) {
            case OUT_FOR_DELIVERY -> {
                // Can only go out for delivery if merchant allows it
                yield rules.controlsStatus(OrderStatus.READY_FOR_PICKUP);
            }
            case CANCELLED -> {
                // Check cancellation rules
                if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                    yield rules.allowsCancellationDuringDelivery();
                }
                yield true;
            }
            default -> true;
        };
    }
    
    private void executePreTransitionLogic(Order order, OrderStatus targetStatus) {
        // Execute any pre-transition business logic
        switch (targetStatus) {
            case PAYMENT_PROCESSING -> {
                // Validate payment information
                if (order.getPaymentInfo() == null) {
                    throw new InvalidOrderStateTransitionException("Payment information required");
                }
            }
            case PREPARING -> {
                // Validate merchant acceptance
                MerchantWorkflowRules rules = getMerchantWorkflowRules(order.getMerchantId());
                if (!rules.autoAcceptOrders() && order.getStatus() == OrderStatus.PAYMENT_CONFIRMED) {
                    // This should be handled by merchant confirmation
                }
            }
            default -> {
                // No specific pre-transition logic
            }
        }
    }
    
    private void executePostTransitionLogic(Order order, OrderStatus targetStatus, String reason) {
        // Execute any post-transition business logic
        switch (targetStatus) {
            case PAYMENT_CONFIRMED -> {
                // Auto-progress if merchant allows it
                if (canAutoProgress(order)) {
                    // Schedule auto-progression (this would be handled by a scheduler)
                }
            }
            case DELIVERED -> {
                // Mark order as completed, trigger any completion workflows
            }
            default -> {
                // No specific post-transition logic
            }
        }
    }
    
    private void executeCancellationLogic(Order order, CancellationReason reason) {
        // Execute pre-cancellation business logic
        switch (reason) {
            case PAYMENT_FAILED -> {
                // Handle payment failure specific logic
            }
            case OUT_OF_STOCK -> {
                // Handle inventory update logic
            }
            default -> {
                // General cancellation logic
            }
        }
    }
    
    private void executePostCancellationLogic(Order order, CancellationReason reason) {
        // Execute post-cancellation logic
        if (reason.allowsAutomaticRefund() && canRefund(order)) {
            // Trigger automatic refund process
            processRefund(order, "Automatic refund due to: " + reason.getDescription());
        }
    }
    
    private void executeRefundLogic(Order order, String reason) {
        // This would integrate with the payment service to process the actual refund
        // For now, we'll just validate that refund is possible
        if (!order.getPaymentInfo().method().supportsRefunds()) {
            throw new InvalidOrderStateTransitionException(
                "Payment method " + order.getPaymentInfo().method() + " does not support refunds");
        }
    }
    
    private void validateStatusTimeout(Order order, MerchantWorkflowRules rules) {
        Duration timeInStatus = Duration.between(order.getUpdatedAt(), Instant.now());
        Duration timeout = rules.getTimeoutForStatus(order.getStatus());
        
        if (timeInStatus.compareTo(timeout) > 0) {
            // Handle timeout
            handleStatusTimeout(order);
        }
    }
    
    private void validateMerchantSpecificRules(Order order, MerchantWorkflowRules rules) {
        // Add merchant-specific validation logic here
        switch (rules.vertical()) {
            case PHARMACY -> {
                // Validate pharmacy-specific rules (e.g., prescription requirements)
            }
            case RESTAURANT -> {
                // Validate restaurant-specific rules (e.g., preparation time limits)
            }
            default -> {
                // General validation
            }
        }
    }
}