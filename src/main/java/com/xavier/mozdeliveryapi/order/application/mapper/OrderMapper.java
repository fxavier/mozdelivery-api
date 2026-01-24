package com.xavier.mozdeliveryapi.order.application.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderRequest;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;

/**
 * Mapper for order-related DTOs and domain objects.
 */
@Component
public class OrderMapper {
    
    /**
     * Map order item requests to domain objects.
     */
    public List<OrderItem> mapOrderItems(List<GuestOrderRequest.OrderItemRequest> itemRequests, Currency currency) {
        return itemRequests.stream()
            .map(item -> mapOrderItem(item, currency))
            .collect(Collectors.toList());
    }
    
    /**
     * Map single order item request to domain object.
     */
    public OrderItem mapOrderItem(GuestOrderRequest.OrderItemRequest itemRequest, Currency currency) {
        Money unitPrice = Money.of(itemRequest.unitPrice(), currency);
        Money totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));
        
        return new OrderItem(
            itemRequest.productId(),
            itemRequest.productName(),
            itemRequest.quantity(),
            unitPrice,
            totalPrice
        );
    }
    
    /**
     * Map delivery address request to domain object.
     */
    public DeliveryAddress mapDeliveryAddress(GuestOrderRequest.DeliveryAddressRequest addressRequest) {
        return new DeliveryAddress(
            addressRequest.street(),
            addressRequest.city(),
            addressRequest.district(),
            addressRequest.postalCode(),
            addressRequest.country(),
            addressRequest.latitude(),
            addressRequest.longitude(),
            addressRequest.deliveryInstructions()
        );
    }
    
    /**
     * Map payment information.
     */
    public PaymentInfo mapPaymentInfo(PaymentMethod paymentMethod, Currency currency, Money amount) {
        return new PaymentInfo(
            paymentMethod,
            generatePaymentReference(), // Generate unique reference
            amount,
            PaymentStatus.PENDING
        );
    }
    
    private String generatePaymentReference() {
        // Generate unique payment reference
        return "PAY-" + System.currentTimeMillis() + "-" + 
               java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}