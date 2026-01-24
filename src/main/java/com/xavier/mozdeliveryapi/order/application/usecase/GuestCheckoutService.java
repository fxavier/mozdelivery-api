package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;

/**
 * Domain service for guest checkout operations.
 */
public interface GuestCheckoutService {
    
    /**
     * Create a guest order with validation.
     */
    Order createGuestOrder(GuestOrderCommand command);
    
    /**
     * Find order by guest tracking token.
     */
    Order findOrderByTrackingToken(GuestTrackingToken token);
    
    /**
     * Validate guest order creation.
     */
    void validateGuestOrderCreation(GuestOrderCommand command);
    
    /**
     * Generate guest tracking information.
     */
    GuestInfo generateGuestInfo(String contactPhone, String contactEmail, String contactName);
    
    /**
     * Resend delivery confirmation code for guest order.
     */
    void resendDeliveryCode(GuestTrackingToken token);
    
    /**
     * Convert guest order to registered customer order.
     */
    void convertGuestToCustomer(GuestTrackingToken token, String customerId);
    
    /**
     * Command for creating guest orders.
     */
    record GuestOrderCommand(
        com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId tenantId,
        GuestInfo guestInfo,
        java.util.List<com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem> items,
        com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress deliveryAddress,
        com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo paymentInfo
    ) {}
}