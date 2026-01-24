package com.xavier.mozdeliveryapi.order.application.usecase;

import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderRequest;
import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderResponse;
import com.xavier.mozdeliveryapi.order.application.dto.GuestTrackingResponse;

/**
 * Application service for guest checkout operations.
 */
public interface GuestCheckoutApplicationService {
    
    /**
     * Create a guest order without registration.
     */
    GuestOrderResponse createGuestOrder(GuestOrderRequest request);
    
    /**
     * Track a guest order using tracking token.
     */
    GuestTrackingResponse trackGuestOrder(String trackingToken);
    
    /**
     * Get order status updates for guest order.
     */
    GuestTrackingResponse getOrderStatusUpdates(String trackingToken);
    
    /**
     * Resend delivery confirmation code for guest order.
     */
    void resendDeliveryCode(String trackingToken);
    
    /**
     * Convert guest order to registered customer order.
     */
    void convertGuestToCustomer(String trackingToken, String customerId);
}