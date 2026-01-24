package com.xavier.mozdeliveryapi.order.integration;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.xavier.mozdeliveryapi.order.application.usecase.OrderWorkflowService;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.service.OrderStateMachine;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;

@SpringBootTest
@ActiveProfiles("test")
class OrderStateMachineIntegrationTest {
    
    @Autowired
    private OrderStateMachine orderStateMachine;
    
    @Autowired
    private OrderWorkflowService orderWorkflowService;
    
    @Test
    void shouldExecuteCompleteOrderWorkflow() {
        // Given
        Order order = createTestOrder();
        
        // When - Process order creation
        orderWorkflowService.processOrderCreation(order);
        
        // Then - Should be in PAYMENT_PROCESSING
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PROCESSING);
        
        // When - Process payment confirmation
        orderWorkflowService.processPaymentConfirmation(order);
        
        // Then - Should be in PAYMENT_CONFIRMED
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
        
        // When - Process order preparation
        orderWorkflowService.processOrderPreparation(order);
        
        // Then - Should be in PREPARING
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARING);
        
        // When - Process ready for pickup
        orderWorkflowService.processOrderReadyForPickup(order);
        
        // Then - Should be in READY_FOR_PICKUP
        assertThat(order.getStatus()).isEqualTo(OrderStatus.READY_FOR_PICKUP);
        
        // When - Process out for delivery
        orderWorkflowService.processOrderOutForDelivery(order);
        
        // Then - Should be in OUT_FOR_DELIVERY
        assertThat(order.getStatus()).isEqualTo(OrderStatus.OUT_FOR_DELIVERY);
        
        // When - Process delivery completion
        orderWorkflowService.processOrderDelivered(order);
        
        // Then - Should be DELIVERED
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }
    
    @Test
    void shouldHandleOrderCancellation() {
        // Given
        Order order = createTestOrder();
        
        // When - Cancel order in PENDING status
        orderWorkflowService.processOrderCancellation(order, CancellationReason.CUSTOMER_REQUEST);
        
        // Then - Should be CANCELLED
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldProvideValidNextStatuses() {
        // Given
        Order order = createTestOrder();
        
        // When
        Set<OrderStatus> nextStatuses = orderWorkflowService.getValidNextStatuses(order);
        
        // Then
        assertThat(nextStatuses).containsExactlyInAnyOrder(
            OrderStatus.PAYMENT_PROCESSING, 
            OrderStatus.PAYMENT_CONFIRMED, 
            OrderStatus.CANCELLED
        );
    }
    
    @Test
    void shouldValidateTransitions() {
        // Given
        Order order = createTestOrder();
        
        // When & Then - Valid transitions
        assertThat(orderWorkflowService.canTransitionTo(order, OrderStatus.PAYMENT_PROCESSING)).isTrue();
        assertThat(orderWorkflowService.canTransitionTo(order, OrderStatus.CANCELLED)).isTrue();
        
        // Invalid transitions
        assertThat(orderWorkflowService.canTransitionTo(order, OrderStatus.DELIVERED)).isFalse();
        assertThat(orderWorkflowService.canTransitionTo(order, OrderStatus.REFUNDED)).isFalse();
    }
    
    @Test
    void shouldHandleCashOnDeliveryAutoProgression() {
        // Given
        Order codOrder = createCashOnDeliveryOrder();
        
        // When
        orderWorkflowService.processOrderCreation(codOrder);
        
        // Then - Should auto-progress to PAYMENT_CONFIRMED for COD
        assertThat(codOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    private Order createTestOrder() {
        return createOrderWithPaymentMethod(PaymentMethod.CREDIT_CARD);
    }
    
    private Order createCashOnDeliveryOrder() {
        return createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
    }
    
    private Order createOrderWithPaymentMethod(PaymentMethod paymentMethod) {
        OrderId orderId = new OrderId(UUID.randomUUID());
        MerchantId merchantId = new MerchantId(UUID.randomUUID());
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        
        List<OrderItem> items = List.of(
            OrderItem.of("test-product", "Test Product", 1, Money.of(10.00, Currency.USD))
        );
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Test St", "Test City", "Test District", "12345", "Test Country", 
            -25.7479, 28.2293
        );
        
        PaymentInfo paymentInfo = PaymentInfo.pending(paymentMethod, Money.of(10.00, Currency.USD));
        
        return new Order(orderId, merchantId, customerId, items, address, paymentInfo);
    }
}