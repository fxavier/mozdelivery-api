package com.xavier.mozdeliveryapi.order.application.dto;

import java.time.Instant;
import java.util.List;

import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

/**
 * Response containing order information.
 */
public record OrderResponse(
    OrderId orderId,
    TenantId tenantId,
    CustomerId customerId,
    List<OrderItemResponse> items,
    DeliveryAddressResponse deliveryAddress,
    OrderStatus status,
    PaymentInfoResponse paymentInfo,
    Money totalAmount,
    Currency currency,
    Instant createdAt,
    Instant updatedAt
) {
    
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getOrderId(),
            order.getTenantId(),
            order.getCustomerId(),
            order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList(),
            DeliveryAddressResponse.from(order.getDeliveryAddress()),
            order.getStatus(),
            PaymentInfoResponse.from(order.getPaymentInfo()),
            order.getTotalAmount(),
            order.getCurrency(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
    
    public static record OrderItemResponse(
        String productId,
        String productName,
        int quantity,
        Money unitPrice,
        Money totalPrice
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice(),
                item.totalPrice()
            );
        }
    }
    
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
    ) {
        public static DeliveryAddressResponse from(DeliveryAddress address) {
            return new DeliveryAddressResponse(
                address.street(),
                address.city(),
                address.district(),
                address.postalCode(),
                address.country(),
                address.latitude(),
                address.longitude(),
                address.deliveryInstructions(),
                address.getFormattedAddress()
            );
        }
    }
    
    public static record PaymentInfoResponse(
        PaymentMethod method,
        String paymentReference,
        Money amount,
        PaymentStatus status
    ) {
        public static PaymentInfoResponse from(PaymentInfo paymentInfo) {
            return new PaymentInfoResponse(
                paymentInfo.method(),
                paymentInfo.paymentReference(),
                paymentInfo.amount(),
                paymentInfo.status()
            );
        }
    }
}