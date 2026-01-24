package com.xavier.mozdeliveryapi.order.application.dto;

import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Request for creating a guest order without registration.
 */
public record GuestOrderRequest(
    MerchantId merchantId,
    GuestContactInfo guestInfo,
    List<OrderItemRequest> items,
    DeliveryAddressRequest deliveryAddress,
    PaymentMethod paymentMethod,
    Currency currency
) implements ValueObject {
    
    public GuestOrderRequest {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(guestInfo, "Guest info cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(deliveryAddress, "Delivery address cannot be null");
        Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
    
    /**
     * Guest contact information for checkout.
     */
    public static record GuestContactInfo(
        String contactPhone,
        String contactEmail,
        String contactName
    ) {
        public GuestContactInfo {
            Objects.requireNonNull(contactPhone, "Contact phone cannot be null");
            Objects.requireNonNull(contactEmail, "Contact email cannot be null");
            Objects.requireNonNull(contactName, "Contact name cannot be null");
            
            if (contactPhone.trim().isEmpty()) {
                throw new IllegalArgumentException("Contact phone cannot be empty");
            }
            
            if (contactEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Contact email cannot be empty");
            }
            
            if (contactName.trim().isEmpty()) {
                throw new IllegalArgumentException("Contact name cannot be empty");
            }
        }
    }
    
    /**
     * Order item for guest checkout.
     */
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
    
    /**
     * Delivery address for guest checkout.
     */
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