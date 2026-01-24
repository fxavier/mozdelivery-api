package com.xavier.mozdeliveryapi.order.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Domain event published when an order is created.
 * Supports both registered customer and guest orders.
 */
public record OrderCreatedEvent(
    OrderId orderId,
    MerchantId merchantId,
    CustomerId customerId, // null for guest orders
    GuestInfo guestInfo, // null for registered customer orders
    Money totalAmount,
    Instant timestamp
) implements DomainEvent {
    
    public static OrderCreatedEvent of(OrderId orderId, MerchantId merchantId, 
                                     CustomerId customerId, Money totalAmount) {
        return new OrderCreatedEvent(orderId, merchantId, customerId, null, totalAmount, Instant.now());
    }
    
    public static OrderCreatedEvent ofGuest(OrderId orderId, MerchantId merchantId, 
                                          GuestInfo guestInfo, Money totalAmount) {
        return new OrderCreatedEvent(orderId, merchantId, null, guestInfo, totalAmount, Instant.now());
    }
    
    /**
     * Check if this is a guest order event.
     */
    public boolean isGuestOrder() {
        return guestInfo != null;
    }
    
    /**
     * Get customer identifier (either customer ID or guest tracking token).
     */
    public String getCustomerIdentifier() {
        if (isGuestOrder()) {
            return guestInfo.trackingToken().getValue();
        } else {
            return customerId.value().toString();
        }
    }
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
    
    @Override
    public String getEventType() {
        return "OrderCreated";
    }
}