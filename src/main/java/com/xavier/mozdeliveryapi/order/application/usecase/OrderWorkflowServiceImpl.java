package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.service.OrderStateMachine;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;

/**
 * Implementation of OrderWorkflowService that delegates to the domain state machine.
 */
@Service
public class OrderWorkflowServiceImpl implements OrderWorkflowService {
    
    private final OrderStateMachine orderStateMachine;
    
    public OrderWorkflowServiceImpl(OrderStateMachine orderStateMachine) {
        this.orderStateMachine = Objects.requireNonNull(orderStateMachine, 
            "Order state machine cannot be null");
    }
    
    @Override
    public void processOrderCreation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // Validate business rules
        orderStateMachine.validateBusinessRules(order);
        
        // Execute auto-progression if applicable
        if (orderStateMachine.canAutoProgress(order)) {
            orderStateMachine.executeAutoProgression(order);
        }
    }
    
    @Override
    public void processPaymentConfirmation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_PROCESSING) {
            orderStateMachine.executeTransition(order, OrderStatus.PAYMENT_CONFIRMED, 
                "Payment confirmed");
            
            // Auto-progress if merchant allows it
            if (orderStateMachine.canAutoProgress(order)) {
                orderStateMachine.executeAutoProgression(order);
            }
        }
    }
    
    @Override
    public void processPaymentFailure(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_PROCESSING) {
            orderStateMachine.cancelOrder(order, CancellationReason.PAYMENT_FAILED, 
                "Payment processing failed");
        }
    }
    
    @Override
    public void processOrderPreparation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_CONFIRMED) {
            orderStateMachine.executeTransition(order, OrderStatus.PREPARING, 
                "Merchant accepted order");
        }
    }
    
    @Override
    public void processOrderReadyForPickup(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PREPARING) {
            orderStateMachine.executeTransition(order, OrderStatus.READY_FOR_PICKUP, 
                "Order preparation completed");
        }
    }
    
    @Override
    public void processOrderOutForDelivery(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.READY_FOR_PICKUP) {
            orderStateMachine.executeTransition(order, OrderStatus.OUT_FOR_DELIVERY, 
                "Courier picked up order");
        }
    }
    
    @Override
    public void processOrderDelivered(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            orderStateMachine.executeTransition(order, OrderStatus.DELIVERED, 
                "Order delivered successfully");
        }
    }
    
    @Override
    public void processOrderCancellation(Order order, CancellationReason reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
        
        orderStateMachine.cancelOrder(order, reason, null);
    }
    
    @Override
    public void processOrderRefund(Order order, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (orderStateMachine.canRefund(order)) {
            orderStateMachine.processRefund(order, reason);
        }
    }
    
    @Override
    public boolean canAutoTransition(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        return orderStateMachine.canAutoProgress(order);
    }
    
    @Override
    public OrderStatus getNextAutomaticStatus(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        return orderStateMachine.getNextAutoStatus(order);
    }
    
    @Override
    public void executeAutoProgression(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        orderStateMachine.executeAutoProgression(order);
    }
    
    @Override
    public Set<OrderStatus> getValidNextStatuses(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        return orderStateMachine.getValidNextStatuses(order);
    }
    
    @Override
    public boolean canTransitionTo(Order order, OrderStatus targetStatus) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(targetStatus, "Target status cannot be null");
        return orderStateMachine.canTransition(order, targetStatus);
    }
    
    @Override
    public void executeTransition(Order order, OrderStatus targetStatus, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(targetStatus, "Target status cannot be null");
        orderStateMachine.executeTransition(order, targetStatus, reason);
    }
    
    @Override
    public void handleOrderTimeout(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        orderStateMachine.handleStatusTimeout(order);
    }
    
    @Override
    public boolean requiresManualIntervention(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        return orderStateMachine.requiresManualIntervention(order);
    }
}