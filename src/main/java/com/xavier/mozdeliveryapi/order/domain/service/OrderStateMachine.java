package com.xavier.mozdeliveryapi.order.domain.service;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

import java.util.Set;

/**
 * Domain service for managing order state transitions and business rules.
 * Implements comprehensive state machine logic with merchant-specific workflows.
 */
public interface OrderStateMachine {
    
    /**
     * Validate if a status transition is allowed for the given order.
     */
    boolean canTransition(Order order, OrderStatus targetStatus);
    
    /**
     * Get all valid next statuses for the given order.
     */
    Set<OrderStatus> getValidNextStatuses(Order order);
    
    /**
     * Execute a status transition with business rule validation.
     */
    void executeTransition(Order order, OrderStatus targetStatus);
    
    /**
     * Execute a status transition with additional context.
     */
    void executeTransition(Order order, OrderStatus targetStatus, String reason);
    
    /**
     * Check if an order can be cancelled from its current status.
     */
    boolean canCancel(Order order);
    
    /**
     * Cancel an order with proper workflow handling.
     */
    void cancelOrder(Order order, CancellationReason reason, String details);
    
    /**
     * Check if an order can be refunded.
     */
    boolean canRefund(Order order);
    
    /**
     * Process refund for an order.
     */
    void processRefund(Order order, String reason);
    
    /**
     * Get merchant-specific workflow rules for the order.
     */
    MerchantWorkflowRules getMerchantWorkflowRules(MerchantId merchantId);
    
    /**
     * Check if automatic status progression is allowed.
     */
    boolean canAutoProgress(Order order);
    
    /**
     * Get the next automatic status for progression.
     */
    OrderStatus getNextAutoStatus(Order order);
    
    /**
     * Execute automatic status progression.
     */
    void executeAutoProgression(Order order);
    
    /**
     * Validate business rules for the current order state.
     */
    void validateBusinessRules(Order order);
    
    /**
     * Check if the order requires manual intervention.
     */
    boolean requiresManualIntervention(Order order);
    
    /**
     * Get timeout duration for current status (in minutes).
     */
    long getStatusTimeoutMinutes(Order order);
    
    /**
     * Handle status timeout for orders.
     */
    void handleStatusTimeout(Order order);
}