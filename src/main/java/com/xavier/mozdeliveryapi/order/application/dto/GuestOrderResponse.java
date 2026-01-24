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
 * Response for guest order creation and tracking.
 */
public record GuestOrderResponse(
    OrderId orderId,
    MerchantId merchantId,
    GuestInfoResponse guestInfo,
    List<OrderItemResponse> items,
    DeliveryAddressResponse deliveryAddress,
    OrderStatus status,
    PaymentInfoResponse paymentInfo,
    Money totalAmount,
    Currency currency,
    String trackingToken,
    String trackingUrl,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static GuestOrderResponse from(Order order, String baseUrl) {
        if (order.getGuestInfo() == null) {
            throw new IllegalArgumentException("Order must have guest info");
        }
        
        GuestInfo guestInfo = order.getGuestInfo();
        String trackingUrl = baseUrl + "/api/public/orders/guest/track?token=" + 
                           guestInfo.trackingToken().getValue();
        
        return new GuestOrderResponse(
            order.getOrderId(),
            order.getMerchantId(),
            GuestInfoResponse.from(guestInfo),
            order.getItems().stream()
                .map(item -> new OrderItemResponse(
                    item.productId(),
                    item.productName(),
                    item.quantity(),
                    item.unitPrice(),
                    item.totalPrice()
                ))
                .toList(),
            new DeliveryAddressResponse(
                order.getDeliveryAddress().street(),
                order.getDeliveryAddress().city(),
                order.getDeliveryAddress().district(),
                order.getDeliveryAddress().postalCode(),
                order.getDeliveryAddress().country(),
                order.getDeliveryAddress().latitude(),
                order.getDeliveryAddress().longitude(),
                order.getDeliveryAddress().deliveryInstructions(),
                order.getDeliveryAddress().getFormattedAddress()
            ),
            order.getStatus(),
            new PaymentInfoResponse(
                order.getPaymentInfo().method(),
                order.getPaymentInfo().paymentReference(),
                order.getPaymentInfo().amount(),
                order.getPaymentInfo().status()
            ),
            order.getTotalAmount(),
            order.getCurrency(),
            guestInfo.trackingToken().getValue(),
            trackingUrl,
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
    
    /**
     * Guest information response.
     */
    public static record GuestInfoResponse(
        String contactPhone,
        String contactEmail,
        String contactName,
        Instant createdAt
    ) {
        public static GuestInfoResponse from(GuestInfo guestInfo) {
            return new GuestInfoResponse(
                guestInfo.contactPhone(),
                guestInfo.contactEmail(),
                guestInfo.contactName(),
                guestInfo.createdAt()
            );
        }
    }
    
    // Reuse existing response records from OrderResponse
    public static record OrderItemResponse(
        String productId,
        String productName,
        int quantity,
        Money unitPrice,
        Money totalPrice
    ) {}
    
    public static record DeliveryAddressResponse(
        String street,
        String city,
        String district,
        String postalCode,
        String country,
        double latitude,
        double longitude,
        String deliveryInstructions,
        String formattedAddress
    ) {}
    
    public static record PaymentInfoResponse(
        com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod method,
        String paymentReference,
        Money amount,
        com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus status
    ) {}
}