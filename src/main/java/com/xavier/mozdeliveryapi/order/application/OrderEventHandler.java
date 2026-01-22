package com.xavier.mozdeliveryapi.order.application;

import java.util.Objects;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.order.domain.OrderCancelledEvent;
import com.xavier.mozdeliveryapi.order.domain.OrderCreatedEvent;
import com.xavier.mozdeliveryapi.order.domain.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.OrderStatusChangedEvent;
import com.xavier.mozdeliveryapi.order.domain.OrderWorkflowService;

/**
 * Event handler for order domain events.
 */
@Component("orderDomainEventHandler")
public class OrderEventHandler {
    
    private final OrderRepository orderRepository;
    private final OrderWorkflowService workflowService;
    
    public OrderEventHandler(OrderRepository orderRepository, OrderWorkflowService workflowService) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
        this.workflowService = Objects.requireNonNull(workflowService, "Workflow service cannot be null");
    }
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        // Find the order and process creation workflow
        orderRepository.findById(event.orderId())
            .ifPresent(workflowService::processOrderCreation);
    }
    
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        // Log status change or trigger additional workflows
        System.out.println("Order " + event.orderId() + " status changed from " + 
                          event.oldStatus() + " to " + event.newStatus());
        
        // Could trigger notifications, analytics updates, etc.
    }
    
    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        // Process cancellation workflow (refunds, notifications, etc.)
        System.out.println("Order " + event.orderId() + " cancelled due to: " + event.reason());
        
        // Could trigger refund processing, inventory updates, etc.
    }
}
