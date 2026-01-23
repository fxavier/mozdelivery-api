package com.xavier.mozdeliveryapi.order.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import java.util.Objects;


/**
 * Value object representing an item in an order.
 */
public record OrderItem(
    String productId,
    String productName,
    int quantity,
    Money unitPrice,
    Money totalPrice
) implements ValueObject {
    
    public OrderItem {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        Objects.requireNonNull(totalPrice, "Total price cannot be null");
        
        if (productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Validate that total price equals unit price * quantity
        Money expectedTotal = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
        if (!totalPrice.equals(expectedTotal)) {
            throw new IllegalArgumentException("Total price must equal unit price * quantity");
        }
    }
    
    public static OrderItem of(String productId, String productName, int quantity, Money unitPrice) {
        Money totalPrice = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
        return new OrderItem(productId, productName, quantity, unitPrice, totalPrice);
    }
}