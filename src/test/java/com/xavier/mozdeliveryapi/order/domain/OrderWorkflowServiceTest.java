package com.xavier.mozdeliveryapi.order.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.order.application.usecase.OrderWorkflowService;
import com.xavier.mozdeliveryapi.order.application.usecase.OrderWorkflowServiceImpl;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;

class OrderWorkflowServiceTest {
    
    private OrderWorkflowService workflowService;
    
    @BeforeEach
    void setUp() {
        workflowService = new OrderWorkflowServiceImpl();
    }
    
    @Test
    void shouldProcessOrderCreationForCashOnDelivery() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        
        // When
        workflowService.processOrderCreation(order);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldProcessOrderCreationForOnlinePayment() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        
        // When
        workflowService.processOrderCreation(order);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PROCESSING);
    }
    
    @Test
    void shouldProcessPaymentConfirmation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        // When
        workflowService.processPaymentConfirmation(order);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldProcessPaymentFailure() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        // When
        workflowService.processPaymentFailure(order);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldProcessOrderPreparation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        
        // When
        workflowService.processOrderPreparation(order);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARING);
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
        assertThat(order.getStatus()).isEqualTo(OrderStatus.READY_FOR_PICKUP);
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
        assertThat(order.getStatus()).isEqualTo(OrderStatus.OUT_FOR_DELIVERY);
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
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }
    
    @Test
    void shouldProcessOrderCancellation() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        
        // When
        workflowService.processOrderCancellation(order, CancellationReason.CUSTOMER_REQUEST);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldIdentifyAutoTransitionForCashOnDelivery() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        
        // When & Then
        assertThat(workflowService.canAutoTransition(order)).isTrue();
        assertThat(workflowService.getNextAutomaticStatus(order)).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldIdentifyAutoTransitionForPaymentConfirmed() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        
        // When & Then
        assertThat(workflowService.canAutoTransition(order)).isTrue();
        assertThat(workflowService.getNextAutomaticStatus(order)).isEqualTo(OrderStatus.PREPARING);
    }
    
    @Test
    void shouldNotAutoTransitionForOnlinePayment() {
        // Given
        Order order = createOrderWithPaymentMethod(PaymentMethod.MPESA);
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        // When & Then
        assertThat(workflowService.canAutoTransition(order)).isFalse();
    }
    
    private Order createOrderWithPaymentMethod(PaymentMethod paymentMethod) {
        OrderId orderId = OrderId.generate();
        TenantId tenantId = TenantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        OrderItem item = OrderItem.of("product-1", "Test Product", 1, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(item);
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        PaymentInfo paymentInfo = PaymentInfo.pending(paymentMethod, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        
        return new Order(orderId, tenantId, customerId, items, address, paymentInfo);
    }
}
