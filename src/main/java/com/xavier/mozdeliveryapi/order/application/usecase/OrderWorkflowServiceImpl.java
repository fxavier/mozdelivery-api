package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;

import java.util.Objects;

import org.springframework.stereotype.Service;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;

/**
 * Implementation of OrderWorkflowService.
 */
@Service
public class OrderWorkflowServiceImpl implements OrderWorkflowService {
    
    @Override
    public void processOrderCreation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        // For cash on delivery, skip payment processing
        if (order.getPaymentInfo().method() == PaymentMethod.CASH_ON_DELIVERY) {
            order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        } else {
            // For other payment methods, move to payment processing
            order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        }
    }
    
    @Override
    public void processPaymentConfirmation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_PROCESSING) {
            order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        }
    }
    
    @Override
    public void processPaymentFailure(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_PROCESSING) {
            order.cancel(CancellationReason.PAYMENT_FAILED);
        }
    }
    
    @Override
    public void processOrderPreparation(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PAYMENT_CONFIRMED) {
            order.updateStatus(OrderStatus.PREPARING);
        }
    }
    
    @Override
    public void processOrderReadyForPickup(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.PREPARING) {
            order.updateStatus(OrderStatus.READY_FOR_PICKUP);
        }
    }
    
    @Override
    public void processOrderOutForDelivery(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.READY_FOR_PICKUP) {
            order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        }
    }
    
    @Override
    public void processOrderDelivered(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            order.updateStatus(OrderStatus.DELIVERED);
        }
    }
    
    @Override
    public void processOrderCancellation(Order order, CancellationReason reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
        
        if (order.canBeCancelled()) {
            order.cancel(reason);
        }
    }
    
    @Override
    public boolean canAutoTransition(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        return switch (order.getStatus()) {
            case PENDING -> order.getPaymentInfo().method() == PaymentMethod.CASH_ON_DELIVERY;
            case PAYMENT_CONFIRMED -> true; // Can auto-transition to PREPARING
            default -> false;
        };
    }
    
    @Override
    public OrderStatus getNextAutomaticStatus(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        return switch (order.getStatus()) {
            case PENDING -> order.getPaymentInfo().method() == PaymentMethod.CASH_ON_DELIVERY 
                ? OrderStatus.PAYMENT_CONFIRMED : OrderStatus.PAYMENT_PROCESSING;
            case PAYMENT_CONFIRMED -> OrderStatus.PREPARING;
            default -> throw new IllegalStateException("No automatic transition available for status: " + order.getStatus());
        };
    }
}