package com.xavier.mozdeliveryapi.order.domain.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.exception.InvalidOrderStateTransitionException;
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
class OrderStateMachineTest {
    
    @Mock
    private MerchantWorkflowService merchantWorkflowService;
    
    private OrderStateMachine orderStateMachine;
    private Order testOrder;
    private MerchantId merchantId;
    private MerchantWorkflowRules defaultRules;
    
    @BeforeEach
    void setUp() {
        orderStateMachine = new OrderStateMachineImpl(merchantWorkflowService);
        
        merchantId = new MerchantId(UUID.randomUUID());
        defaultRules = MerchantWorkflowRules.defaultForVertical(merchantId, Vertical.RESTAURANT);
        
        when(merchantWorkflowService.getWorkflowRules(merchantId)).thenReturn(defaultRules);
        
        // Create test order
        testOrder = createTestOrder();
    }
    
    @Test
    void shouldAllowValidTransitions() {
        // Test PENDING -> PAYMENT_PROCESSING
        assertThat(orderStateMachine.canTransition(testOrder, OrderStatus.PAYMENT_PROCESSING)).isTrue();
        
        // Execute transition
        orderStateMachine.executeTransition(testOrder, OrderStatus.PAYMENT_PROCESSING);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_PROCESSING);
        
        // Test PAYMENT_PROCESSING -> PAYMENT_CONFIRMED
        assertThat(orderStateMachine.canTransition(testOrder, OrderStatus.PAYMENT_CONFIRMED)).isTrue();
        orderStateMachine.executeTransition(testOrder, OrderStatus.PAYMENT_CONFIRMED);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldRejectInvalidTransitions() {
        // Cannot go directly from PENDING to DELIVERED
        assertThat(orderStateMachine.canTransition(testOrder, OrderStatus.DELIVERED)).isFalse();
        
        // Should throw exception when attempting invalid transition
        assertThatThrownBy(() -> 
            orderStateMachine.executeTransition(testOrder, OrderStatus.DELIVERED))
            .isInstanceOf(InvalidOrderStateTransitionException.class);
    }
    
    @Test
    void shouldReturnValidNextStatuses() {
        Set<OrderStatus> nextStatuses = orderStateMachine.getValidNextStatuses(testOrder);
        
        assertThat(nextStatuses).containsExactlyInAnyOrder(
            OrderStatus.PAYMENT_PROCESSING, 
            OrderStatus.PAYMENT_CONFIRMED, 
            OrderStatus.CANCELLED
        );
    }
    
    @Test
    void shouldHandleCancellation() {
        // Order should be cancellable in PENDING status
        assertThat(orderStateMachine.canCancel(testOrder)).isTrue();
        
        // Cancel the order
        orderStateMachine.cancelOrder(testOrder, CancellationReason.CUSTOMER_REQUEST, "Customer changed mind");
        
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldPreventCancellationDuringDeliveryForRestaurants() {
        // Move order to OUT_FOR_DELIVERY
        progressOrderToStatus(testOrder, OrderStatus.OUT_FOR_DELIVERY);
        
        // Restaurant rules don't allow cancellation during delivery
        assertThat(orderStateMachine.canCancel(testOrder)).isFalse();
        
        assertThatThrownBy(() -> 
            orderStateMachine.cancelOrder(testOrder, CancellationReason.CUSTOMER_REQUEST, "Test"))
            .isInstanceOf(InvalidOrderStateTransitionException.class);
    }
    
    @Test
    void shouldAllowCancellationDuringDeliveryForGrocery() {
        // Create grocery merchant rules
        MerchantWorkflowRules groceryRules = MerchantWorkflowRules.defaultForVertical(merchantId, Vertical.GROCERY);
        when(merchantWorkflowService.getWorkflowRules(merchantId)).thenReturn(groceryRules);
        
        // Move order to OUT_FOR_DELIVERY
        progressOrderToStatus(testOrder, OrderStatus.OUT_FOR_DELIVERY);
        
        // Grocery rules allow cancellation during delivery
        assertThat(orderStateMachine.canCancel(testOrder)).isTrue();
        
        orderStateMachine.cancelOrder(testOrder, CancellationReason.CUSTOMER_REQUEST, "Test");
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldHandleRefunds() {
        // Move order to DELIVERED
        progressOrderToStatus(testOrder, OrderStatus.DELIVERED);
        
        // Should be able to refund delivered order
        assertThat(orderStateMachine.canRefund(testOrder)).isTrue();
        
        orderStateMachine.processRefund(testOrder, "Quality issue");
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.REFUNDED);
    }
    
    @Test
    void shouldNotRefundCashOnDeliveryOrders() {
        // Create order with cash on delivery
        Order codOrder = createTestOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        progressOrderToStatus(codOrder, OrderStatus.DELIVERED);
        
        // Cash on delivery doesn't support refunds
        assertThat(orderStateMachine.canRefund(codOrder)).isFalse();
        
        assertThatThrownBy(() -> 
            orderStateMachine.processRefund(codOrder, "Test"))
            .isInstanceOf(InvalidOrderStateTransitionException.class);
    }
    
    @Test
    void shouldHandleAutoProgression() {
        // Create cash on delivery order
        Order codOrder = createTestOrderWithPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        
        // Should be able to auto-progress
        assertThat(orderStateMachine.canAutoProgress(codOrder)).isTrue();
        assertThat(orderStateMachine.getNextAutoStatus(codOrder)).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
        
        orderStateMachine.executeAutoProgression(codOrder);
        assertThat(codOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldDetectManualInterventionRequired() {
        // Move to PAYMENT_CONFIRMED (requires merchant action for restaurants)
        progressOrderToStatus(testOrder, OrderStatus.PAYMENT_CONFIRMED);
        
        // Should require manual intervention for restaurant orders
        assertThat(orderStateMachine.requiresManualIntervention(testOrder)).isTrue();
    }
    
    @Test
    void shouldHandleTimeouts() {
        // Move order to PAYMENT_PROCESSING first
        orderStateMachine.executeTransition(testOrder, OrderStatus.PAYMENT_PROCESSING);
        
        // This would typically be called by a scheduler
        orderStateMachine.handleStatusTimeout(testOrder);
        
        // For PAYMENT_PROCESSING status, timeout should cancel the order
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    private Order createTestOrder() {
        return createTestOrderWithPaymentMethod(PaymentMethod.CREDIT_CARD);
    }
    
    private Order createTestOrderWithPaymentMethod(PaymentMethod paymentMethod) {
        OrderId orderId = new OrderId(UUID.randomUUID());
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        
        List<OrderItem> items = List.of(
            new OrderItem(
                "test-product-id",
                "Test Item",
                1,
                Money.of(10.00, Currency.USD),
                Money.of(10.00, Currency.USD)
            )
        );
        
        DeliveryAddress address = new DeliveryAddress(
            "123 Test St",
            "Test City",
            "Test District",
            "12345",
            "Test Country",
            -25.7479, 28.2293, // Pretoria coordinates
            null // No delivery instructions
        );
        
        PaymentInfo paymentInfo = PaymentInfo.pending(paymentMethod, Money.of(10.00, Currency.USD));
        
        return new Order(orderId, merchantId, customerId, items, address, paymentInfo);
    }
    
    private void progressOrderToStatus(Order order, OrderStatus targetStatus) {
        // Simple progression through the standard workflow
        while (order.getStatus() != targetStatus) {
            OrderStatus currentStatus = order.getStatus();
            
            switch (currentStatus) {
                case PENDING -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.PAYMENT_PROCESSING);
                    }
                }
                case PAYMENT_PROCESSING -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.PAYMENT_CONFIRMED);
                    }
                }
                case PAYMENT_CONFIRMED -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.PREPARING);
                    }
                }
                case PREPARING -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.READY_FOR_PICKUP);
                    }
                }
                case READY_FOR_PICKUP -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.OUT_FOR_DELIVERY);
                    }
                }
                case OUT_FOR_DELIVERY -> {
                    if (targetStatus == OrderStatus.CANCELLED) {
                        orderStateMachine.executeTransition(order, OrderStatus.CANCELLED);
                    } else {
                        orderStateMachine.executeTransition(order, OrderStatus.DELIVERED);
                    }
                }
                case DELIVERED -> {
                    if (targetStatus == OrderStatus.REFUNDED) {
                        orderStateMachine.executeTransition(order, OrderStatus.REFUNDED);
                    } else {
                        throw new IllegalStateException("Cannot progress from DELIVERED to " + targetStatus);
                    }
                }
                case CANCELLED -> {
                    if (targetStatus == OrderStatus.REFUNDED) {
                        orderStateMachine.executeTransition(order, OrderStatus.REFUNDED);
                    } else {
                        throw new IllegalStateException("Cannot progress from CANCELLED to " + targetStatus);
                    }
                }
                default -> throw new IllegalStateException("Cannot progress from " + currentStatus + " to " + targetStatus);
            }
        }
    }
}