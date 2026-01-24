package com.xavier.mozdeliveryapi.order.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xavier.mozdeliveryapi.order.application.usecase.OrderWorkflowService;
import com.xavier.mozdeliveryapi.order.application.usecase.OrderWorkflowServiceImpl;
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

@ExtendWith(MockitoExtension.class)
class OrderWorkflowServiceTest {
    
    @Mock
    private OrderStateMachine orderStateMachine;
    
    private OrderWorkflowService workflowService;
    
    @BeforeEach
    void setUp() {
        workflowService = new OrderWorkflowServiceImpl(orderStateMachine);
    }
    
    @Test
    void shouldProcessOrderCreationForCashOnDelivery() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        when(orderStateMachine.canAutoProgress(order)).thenReturn(true);
        
        // When
        workflowService.processOrderCreation(order);
        
        // Then
        verify(orderStateMachine).validateBusinessRules(order);
        verify(orderStateMachine).executeAutoProgression(order);
    }
    
    @Test
    void shouldProcessOrderCreationForOnlinePayment() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        when(orderStateMachine.canAutoProgress(order)).thenReturn(false);
        
        // When
        workflowService.processOrderCreation(order);
        
        // Then
        verify(orderStateMachine).validateBusinessRules(order);
        verify(orderStateMachine, never()).executeAutoProgression(order);
    }
    
    @Test
    void shouldProcessPaymentConfirmation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        when(orderStateMachine.canAutoProgress(order)).thenReturn(true);
        
        // When
        workflowService.processPaymentConfirmation(order);
        
        // Then
        verify(orderStateMachine).executeTransition(order, OrderStatus.PAYMENT_CONFIRMED, "Payment confirmed");
        verify(orderStateMachine).executeAutoProgression(order);
    }
    
    @Test
    void shouldProcessPaymentFailure() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        // When
        workflowService.processPaymentFailure(order);
        
        // Then
        verify(orderStateMachine).cancelOrder(order, CancellationReason.PAYMENT_FAILED, "Payment processing failed");
    }
    
    @Test
    void shouldProcessOrderPreparation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        
        // When
        workflowService.processOrderPreparation(order);
        
        // Then
        verify(orderStateMachine).executeTransition(order, OrderStatus.PREPARING, "Merchant accepted order");
    }
    
    @Test
    void shouldProcessOrderReadyForPickup() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        
        // When
        workflowService.processOrderReadyForPickup(order);
        
        // Then
        verify(orderStateMachine).executeTransition(order, OrderStatus.READY_FOR_PICKUP, "Order preparation completed");
    }
    
    @Test
    void shouldProcessOrderOutForDelivery() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY_FOR_PICKUP);
        
        // When
        workflowService.processOrderOutForDelivery(order);
        
        // Then
        verify(orderStateMachine).executeTransition(order, OrderStatus.OUT_FOR_DELIVERY, "Courier picked up order");
    }
    
    @Test
    void shouldProcessOrderDelivered() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY_FOR_PICKUP);
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        
        // When
        workflowService.processOrderDelivered(order);
        
        // Then
        verify(orderStateMachine).executeTransition(order, OrderStatus.DELIVERED, "Order delivered successfully");
    }
    
    @Test
    void shouldProcessOrderCancellation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        
        // When
        workflowService.processOrderCancellation(order, CancellationReason.CUSTOMER_REQUEST);
        
        // Then
        verify(orderStateMachine).cancelOrder(order, CancellationReason.CUSTOMER_REQUEST, null);
    }
    
    @Test
    void shouldProcessOrderRefund() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        when(orderStateMachine.canRefund(order)).thenReturn(true);
        
        // When
        workflowService.processOrderRefund(order, "Quality issue");
        
        // Then
        verify(orderStateMachine).processRefund(order, "Quality issue");
    }
    
    @Test
    void shouldIdentifyAutoTransition() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        when(orderStateMachine.canAutoProgress(order)).thenReturn(true);
        
        // When & Then
        assertThat(workflowService.canAutoTransition(order)).isTrue();
        verify(orderStateMachine).canAutoProgress(order);
    }
    
    @Test
    void shouldGetNextAutomaticStatus() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        when(orderStateMachine.getNextAutoStatus(order)).thenReturn(OrderStatus.PAYMENT_CONFIRMED);
        
        // When & Then
        assertThat(workflowService.getNextAutomaticStatus(order)).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
        verify(orderStateMachine).getNextAutoStatus(order);
    }
    
    @Test
    void shouldGetValidNextStatuses() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        Set<OrderStatus> expectedStatuses = Set.of(OrderStatus.PAYMENT_PROCESSING, OrderStatus.CANCELLED);
        when(orderStateMachine.getValidNextStatuses(order)).thenReturn(expectedStatuses);
        
        // When & Then
        assertThat(workflowService.getValidNextStatuses(order)).isEqualTo(expectedStatuses);
        verify(orderStateMachine).getValidNextStatuses(order);
    }
    
    private Order createOrderWithPaymentMethod(PaymentMethod paymentMethod) {
        OrderId orderId = OrderId.generate();
        MerchantId merchantId = MerchantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        OrderItem item = OrderItem.of("product-1", "Test Product", 1, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(item);
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        PaymentInfo paymentInfo = PaymentInfo.pending(paymentMethod, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        
        return new Order(orderId, merchantId, customerId, items, address, paymentInfo);
    }
}
