package com.xavier.mozdeliveryapi.order.application;

import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Request for creating a new order.
 */
public record CreateOrderRequest(
    TenantId tenantId,
    CustomerId customerId,
    List<OrderItemRequest> items,
    DeliveryAddressRequest deliveryAddress,
    PaymentMethod paymentMethod,
    Currency currency
) implements ValueObject {
    
    public CreateOrderRequest {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(deliveryAddress, "Delivery address cannot be null");
        Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
    
    public static record OrderItemRequest(
        String productId,
        String productName,
        int quantity,
        java.math.BigDecimal unitPrice
    ) {
        public OrderItemRequest {
            Objects.requireNonNull(productId, "Product ID cannot be null");
            Objects.requireNonNull(productName, "Product name cannot be null");
            Objects.requireNonNull(unitPrice, "Unit price cannot be null");
            
            if (productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            
            if (productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            if (unitPrice.compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative");
            }
        }
    }
    
    public static record DeliveryAddressRequest(
        String street,
        String city,
        String district,
        String postalCode,
        String country,
        double latitude,
        double longitude,
        String deliveryInstructions
    ) {
        public DeliveryAddressRequest {
            Objects.requireNonNull(street, "Street cannot be null");
            Objects.requireNonNull(city, "City cannot be null");
            Objects.requireNonNull(country, "Country cannot be null");
            
            if (street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street cannot be empty");
            }
            
            if (city.trim().isEmpty()) {
                throw new IllegalArgumentException("City cannot be empty");
            }
            
            if (country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country cannot be empty");
            }
            
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90");
            }
            
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180");
            }
        }
    }
}