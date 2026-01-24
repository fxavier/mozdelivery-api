package com.xavier.mozdeliveryapi.order.application.dto;

import java.time.Instant;
import java.util.List;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Response for guest order tracking.
 */
public record GuestTrackingResponse(
    OrderId orderId,
    MerchantId merchantId,
    String merchantName,
    GuestContactInfo guestInfo,
    List<OrderItemSummary> items,
    DeliveryAddressSummary deliveryAddress,
    OrderStatus status,
    String statusDescription,
    Money totalAmount,
    Currency currency,
    Instant createdAt,
    Instant updatedAt,
    Instant estimatedDelivery,
    boolean canCancel,
    boolean canResendCode,
    String trackingUrl
) {
    
    public static GuestTrackingResponse from(Order order, String merchantName) {
        if (order.getGuestInfo() == null) {
            throw new IllegalArgumentException("Order must have guest info");
        }
        
        String baseUrl = "http://localhost:8080"; // This should come from configuration
        String trackingUrl = baseUrl + "/api/public/orders/guest/track?token=" + 
                           order.getGuestInfo().trackingToken().getValue();
        
        return new GuestTrackingResponse(
            order.getOrderId(),
            order.getMerchantId(),
            merchantName,
            GuestContactInfo.from(order.getGuestInfo()),
            order.getItems().stream()
                .map(OrderItemSummary::from)
                .toList(),
            DeliveryAddressSummary.from(order.getDeliveryAddress()),
            order.getStatus(),
            getStatusDescription(order.getStatus()),
            order.getTotalAmount(),
            order.getCurrency(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            calculateEstimatedDelivery(order),
            order.canBeCancelled(),
            canResendDeliveryCode(order.getStatus()),
            trackingUrl
        );
    }
    
    private static String getStatusDescription(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Order received and being processed";
            case PAYMENT_PROCESSING -> "Processing payment";
            case PAYMENT_CONFIRMED -> "Payment confirmed, preparing order";
            case PREPARING -> "Order is being prepared";
            case READY_FOR_PICKUP -> "Order ready for pickup";
            case OUT_FOR_DELIVERY -> "Order is on the way";
            case DELIVERED -> "Order delivered successfully";
            case CANCELLED -> "Order has been cancelled";
            case REFUNDED -> "Order has been refunded";
        };
    }
    
    private static Instant calculateEstimatedDelivery(Order order) {
        // Enhanced estimation based on order status
        return switch (order.getStatus()) {
            case PENDING, PAYMENT_PROCESSING -> 
                order.getCreatedAt().plusSeconds(3600 * 2); // 2 hours for processing + delivery
            case PAYMENT_CONFIRMED, PREPARING -> 
                order.getCreatedAt().plusSeconds(3600); // 1 hour for preparation + delivery
            case READY_FOR_PICKUP -> 
                order.getUpdatedAt().plusSeconds(1800); // 30 minutes for pickup + delivery
            case OUT_FOR_DELIVERY -> 
                order.getUpdatedAt().plusSeconds(900); // 15 minutes for delivery
            case DELIVERED -> 
                order.getUpdatedAt(); // Already delivered
            default -> 
                order.getCreatedAt().plusSeconds(3600); // Default 1 hour
        };
    }
    
    /**
     * Check if delivery code can be resent for the given order status.
     */
    private static boolean canResendDeliveryCode(OrderStatus status) {
        return switch (status) {
            case PAYMENT_CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY -> true;
            default -> false;
        };
    }
    
    /**
     * Guest contact information for tracking.
     */
    public static record GuestContactInfo(
        String contactName,
        String contactPhone
    ) {
        public static GuestContactInfo from(GuestInfo guestInfo) {
            return new GuestContactInfo(
                guestInfo.contactName(),
                guestInfo.contactPhone()
            );
        }
    }
    
    /**
     * Order item summary for tracking.
     */
    public static record OrderItemSummary(
        String productName,
        int quantity,
        Money totalPrice
    ) {
        public static OrderItemSummary from(com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem item) {
            return new OrderItemSummary(
                item.productName(),
                item.quantity(),
                item.totalPrice()
            );
        }
    }
    
    /**
     * Delivery address summary for tracking.
     */
    public static record DeliveryAddressSummary(
        String formattedAddress,
        String deliveryInstructions
    ) {
        public static DeliveryAddressSummary from(com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress address) {
            return new DeliveryAddressSummary(
                address.getFormattedAddress(),
                address.deliveryInstructions()
            );
        }
    }
}