package com.xavier.mozdeliveryapi.order.application.dto;

import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import java.time.Instant;
import java.util.List;

/**
 * Response for guest order tracking.
 */
public record GuestTrackingResponse(
    OrderId orderId,
    TenantId tenantId,
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
    boolean canCancel
) {
    
    public static GuestTrackingResponse from(Order order, String merchantName) {
        if (order.getGuestInfo() == null) {
            throw new IllegalArgumentException("Order must have guest info");
        }
        
        return new GuestTrackingResponse(
            order.getOrderId(),
            order.getTenantId(),
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
            order.canBeCancelled()
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
        // Simple estimation - in real implementation this would use delivery service
        return order.getCreatedAt().plusSeconds(3600); // 1 hour estimate
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