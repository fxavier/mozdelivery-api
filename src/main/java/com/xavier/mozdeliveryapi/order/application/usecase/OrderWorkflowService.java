package com.xavier.mozdeliveryapi.order.application.usecase;
import java.util.Set;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;

/**
 * Service for managing order workflow and status transitions.
 * This is the application layer service that orchestrates order workflows.
 */
public interface OrderWorkflowService {
    
    /**
     * Process order creation workflow.
     */
    void processOrderCreation(Order order);
    
    /**
     * Process payment confirmation workflow.
     */
    void processPaymentConfirmation(Order order);
    
    /**
     * Process payment failure workflow.
     */
    void processPaymentFailure(Order order);
    
    /**
     * Process order preparation workflow.
     */
    void processOrderPreparation(Order order);
    
    /**
     * Process order ready for pickup workflow.
     */
    void processOrderReadyForPickup(Order order);
    
    /**
     * Process order out for delivery workflow.
     */
    void processOrderOutForDelivery(Order order);
    
    /**
     * Process order delivery completion workflow.
     */
    void processOrderDelivered(Order order);
    
    /**
     * Process order cancellation workflow.
     */
    void processOrderCancellation(Order order, CancellationReason reason);
    
    /**
     * Process order refund workflow.
     */
    void processOrderRefund(Order order, String reason);
    
    /**
     * Check if order can be automatically transitioned to next status.
     */
    boolean canAutoTransition(Order order);
    
    /**
     * Get the next automatic status for an order.
     */
    OrderStatus getNextAutomaticStatus(Order order);
    
    /**
     * Execute automatic progression for an order.
     */
    void executeAutoProgression(Order order);
    
    /**
     * Get all valid next statuses for an order.
     */
    Set<OrderStatus> getValidNextStatuses(Order order);
    
    /**
     * Validate if a status transition is allowed.
     */
    boolean canTransitionTo(Order order, OrderStatus targetStatus);
    
    /**
     * Execute a manual status transition.
     */
    void executeTransition(Order order, OrderStatus targetStatus, String reason);
    
    /**
     * Handle order timeout scenarios.
     */
    void handleOrderTimeout(Order order);
    
    /**
     * Check if order requires manual intervention.
     */
    boolean requiresManualIntervention(Order order);
}