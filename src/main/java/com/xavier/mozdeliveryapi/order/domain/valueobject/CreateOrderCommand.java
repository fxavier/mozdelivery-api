package com.xavier.mozdeliveryapi.order.domain.valueobject;

import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Command for creating a new order.
 */
public record CreateOrderCommand(
    MerchantId merchantId,
    CustomerId customerId,
    List<OrderItem> items,
    DeliveryAddress deliveryAddress,
    PaymentMethod paymentMethod,
    Currency currency
) implements ValueObject {
    
    public CreateOrderCommand {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(deliveryAddress, "Delivery address cannot be null");
        Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
}