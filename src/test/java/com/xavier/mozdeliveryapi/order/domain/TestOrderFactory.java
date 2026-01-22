package com.xavier.mozdeliveryapi.order.domain;

import java.math.BigDecimal;
import java.util.List;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Factory for creating test orders.
 */
public class TestOrderFactory {
    
    public static Order createOrderWithItems(List<OrderItem> items) {
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
    
    public static Order createValidOrder() {
        OrderItem item = OrderItem.of("product-1", "Test Product", 2, 
            Money.of(BigDecimal.valueOf(10.00), Currency.USD));
        return createOrderWithItems(List.of(item));
    }
}