package com.xavier.mozdeliveryapi.order.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.Order;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.PaymentInfo;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Mapper between Order domain objects and OrderEntity.
 */
@Component
public class OrderMapper {
    
    public OrderEntity toEntity(Order order) {
        return new OrderEntity(
            order.getOrderId().value(),
            order.getTenantId().value(),
            order.getCustomerId().value(),
            mapItems(order.getItems()),
            mapDeliveryAddress(order.getDeliveryAddress()),
            order.getStatus(),
            mapPaymentInfo(order.getPaymentInfo()),
            order.getTotalAmount().amount(),
            order.getCurrency(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
    
    public Order toDomain(OrderEntity entity) {
        return new Order(
            OrderId.of(entity.getId()),
            TenantId.of(entity.getTenantId()),
            CustomerId.of(entity.getCustomerId()),
            mapItemsFromData(entity.getItems()),
            mapDeliveryAddressFromData(entity.getDeliveryAddress()),
            entity.getStatus(),
            mapPaymentInfoFromData(entity.getPaymentInfo()),
            Money.of(entity.getTotalAmount(), entity.getCurrency()),
            entity.getCurrency(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    private List<OrderEntity.OrderItemData> mapItems(List<OrderItem> items) {
        return items.stream()
            .map(item -> new OrderEntity.OrderItemData(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice().amount(),
                item.totalPrice().amount(),
                item.totalPrice().currency().getCode()
            ))
            .collect(Collectors.toList());
    }
    
    private List<OrderItem> mapItemsFromData(List<OrderEntity.OrderItemData> itemsData) {
        return itemsData.stream()
            .map(data -> new OrderItem(
                data.productId(),
                data.productName(),
                data.quantity(),
                Money.of(data.unitPrice(), Currency.fromCode(data.currency())),
                Money.of(data.totalPrice(), Currency.fromCode(data.currency()))
            ))
            .collect(Collectors.toList());
    }
    
    private OrderEntity.DeliveryAddressData mapDeliveryAddress(DeliveryAddress address) {
        return new OrderEntity.DeliveryAddressData(
            address.street(),
            address.city(),
            address.district(),
            address.postalCode(),
            address.country(),
            address.latitude(),
            address.longitude(),
            address.deliveryInstructions()
        );
    }
    
    private DeliveryAddress mapDeliveryAddressFromData(OrderEntity.DeliveryAddressData data) {
        return new DeliveryAddress(
            data.street(),
            data.city(),
            data.district(),
            data.postalCode(),
            data.country(),
            data.latitude(),
            data.longitude(),
            data.deliveryInstructions()
        );
    }
    
    private OrderEntity.PaymentInfoData mapPaymentInfo(PaymentInfo paymentInfo) {
        return new OrderEntity.PaymentInfoData(
            paymentInfo.method().name(),
            paymentInfo.paymentReference(),
            paymentInfo.amount().amount(),
            paymentInfo.amount().currency().getCode(),
            paymentInfo.status().name()
        );
    }
    
    private PaymentInfo mapPaymentInfoFromData(OrderEntity.PaymentInfoData data) {
        return new PaymentInfo(
            PaymentMethod.valueOf(data.method()),
            data.paymentReference(),
            Money.of(data.amount(), Currency.fromCode(data.currency())),
            PaymentStatus.valueOf(data.status())
        );
    }
}