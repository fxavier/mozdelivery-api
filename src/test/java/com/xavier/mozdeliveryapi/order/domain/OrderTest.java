package com.xavier.mozdeliveryapi.order.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;

class OrderTest {
    
    @Test
    void shouldCreateOrderWithValidData() {
        // Given
        OrderId orderId = OrderId.generate();
        TenantId tenantId = TenantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        OrderItem item = OrderItem.of("product-1", "Test Product", 2, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(item);
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        PaymentInfo paymentInfo = PaymentInfo.pending(PaymentMethod.MPESA, 
            Money.of(BigDecimal.valueOf(20.00), Currency.USD));
        
        // When
        Order order = new Order(orderId, tenantId, customerId, items, address, paymentInfo);
        
        // Then
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getTenantId()).isEqualTo(tenantId);
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.isActive()).isTrue();
        assertThat(order.canBeCancelled()).isTrue();
    }
    
    @Test
    void shouldThrowExceptionWhenPaymentAmountDoesNotMatchTotal() {
        // Given
        OrderId orderId = OrderId.generate();
        TenantId tenantId = TenantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        OrderItem item = OrderItem.of("product-1", "Test Product", 2, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(item);
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        // Wrong payment amount
        PaymentInfo paymentInfo = PaymentInfo.pending(PaymentMethod.MPESA, 
            Money.of(BigDecimal.valueOf(15.00), Currency.USD));
        
        // When & Then
        assertThatThrownBy(() -> new Order(orderId, tenantId, customerId, items, address, paymentInfo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Payment amount must match order total");
    }
    
    @Test
    void shouldUpdateStatusSuccessfully() {
        // Given
        Order order = createValidOrder();
        
        // When
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PROCESSING);
    }
    
    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Given
        Order order = createValidOrder();
        
        // When & Then
        assertThatThrownBy(() -> order.updateStatus(OrderStatus.DELIVERED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from PENDING to DELIVERED");
    }
    
    @Test
    void shouldCancelOrderSuccessfully() {
        // Given
        Order order = createValidOrder();
        
        // When
        order.cancel(CancellationReason.CUSTOMER_REQUEST, "Customer changed mind");
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.isActive()).isFalse();
        assertThat(order.canBeCancelled()).isFalse();
    }
    
    @Test
    void shouldThrowExceptionWhenCancellingNonCancellableOrder() {
        // Given
        Order order = createValidOrder();
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY_FOR_PICKUP);
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.updateStatus(OrderStatus.DELIVERED);
        
        // When & Then
        assertThatThrownBy(() -> order.cancel(CancellationReason.CUSTOMER_REQUEST))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel order in status DELIVERED");
    }
    
    @Test
    void shouldUpdatePaymentInfoAndTransitionStatus() {
        // Given
        Order order = createValidOrder();
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        PaymentInfo completedPayment = order.getPaymentInfo()
            .withStatus(PaymentStatus.COMPLETED)
            .withReference("payment-ref-123");
        
        // When
        order.updatePaymentInfo(completedPayment);
        
        // Then
        assertThat(order.getPaymentInfo().status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getPaymentInfo().paymentReference()).isEqualTo("payment-ref-123");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
    }
    
    @Test
    void shouldCancelOrderWhenPaymentFails() {
        // Given
        Order order = createValidOrder();
        order.updateStatus(OrderStatus.PAYMENT_PROCESSING);
        
        PaymentInfo failedPayment = order.getPaymentInfo()
            .withStatus(PaymentStatus.FAILED);
        
        // When
        order.updatePaymentInfo(failedPayment);
        
        // Then
        assertThat(order.getPaymentInfo().status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
    
    @Test
    void shouldCalculateItemCountCorrectly() {
        // Given
        OrderItem item1 = OrderItem.of("product-1", "Product 1", 2, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        OrderItem item2 = OrderItem.of("product-2", "Product 2", 3, 
            Money.of(BigDecimal.valueOf(5.00), Currency.USD));
        
        List<OrderItem> items = List.of(item1, item2);
        Order order = createOrderWithItems(items);
        
        // When & Then
        assertThat(order.getItemCount()).isEqualTo(5); // 2 + 3
    }
    
    private Order createValidOrder() {
        OrderId orderId = OrderId.generate();
        TenantId tenantId = TenantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        OrderItem item = OrderItem.of("product-1", "Test Product", 2, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        List<OrderItem> items = List.of(item);
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        PaymentInfo paymentInfo = PaymentInfo.pending(PaymentMethod.MPESA, 
            Money.of(BigDecimal.valueOf(20.00), Currency.USD));
        
        return new Order(orderId, tenantId, customerId, items, address, paymentInfo);
    }
    
    private Order createOrderWithItems(List<OrderItem> items) {
        OrderId orderId = OrderId.generate();
        TenantId tenantId = TenantId.generate();
        CustomerId customerId = CustomerId.generate();
        
        DeliveryAddress address = DeliveryAddress.of(
            "123 Main St", "Maputo", "Maputo City", "1000", "Mozambique", 
            -25.9692, 32.5732);
        
        // Calculate total
        Money total = Money.zero(Currency.USD);
        for (OrderItem item : items) {
            total = total.add(item.totalPrice());
        }
        
        PaymentInfo paymentInfo = PaymentInfo.pending(PaymentMethod.MPESA, total);
        
        return new Order(orderId, tenantId, customerId, items, address, paymentInfo);
    }
}
