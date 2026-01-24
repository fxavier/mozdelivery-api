package com.xavier.mozdeliveryapi.notification.application.usecase;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.application.usecase.NotificationService;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;
import com.xavier.mozdeliveryapi.order.domain.event.OrderCancelledEvent;
import com.xavier.mozdeliveryapi.order.domain.event.OrderCreatedEvent;
import com.xavier.mozdeliveryapi.order.domain.event.OrderStatusChangedEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * Event handler for order-related events that trigger notifications.
 */
@Component("orderNotificationEventHandler")
public class OrderEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);
    
    private final NotificationService notificationService;
    private final TenantResolutionService tenantResolutionService;
    
    public OrderEventHandler(NotificationService notificationService, TenantResolutionService tenantResolutionService) {
        this.notificationService = notificationService;
        this.tenantResolutionService = tenantResolutionService;
    }
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Handling OrderCreatedEvent for order: {}", event.orderId());
        
        try {
            // Create SMS notification for order creation
            notificationService.createNotification(
                event.merchantId(),
                Recipient.phone("customer_phone_" + event.customerId(), "Customer"),
                NotificationChannel.SMS,
                "order_created",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "totalAmount", event.totalAmount().amount().toString(),
                    "currency", event.totalAmount().currency().toString(),
                    "estimatedDelivery", "30-45 minutes"
                ),
                NotificationPriority.NORMAL
            );
            
            // Create push notification for order creation
            notificationService.createNotification(
                event.merchantId(),
                Recipient.deviceToken("device_token_" + event.customerId(), "Customer"),
                NotificationChannel.PUSH_NOTIFICATION,
                "order_created",
                Map.of(
                    "customerName", "Valued Customer",
                    "orderNumber", event.orderId().toString(),
                    "totalAmount", event.totalAmount().amount().toString(),
                    "currency", event.totalAmount().currency().toString(),
                    "estimatedDelivery", "30-45 minutes"
                ),
                NotificationPriority.NORMAL
            );
            
            logger.info("Order creation notifications created successfully for order: {}", event.orderId());
            
        } catch (Exception e) {
            logger.error("Failed to create order creation notifications for order: {}", event.orderId(), e);
        }
    }
    
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        logger.info("Handling OrderStatusChangedEvent for order: {} - {} -> {}", 
                   event.orderId(), event.oldStatus(), event.newStatus());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromOrderId(event.orderId().toString());
            String statusMessage = getStatusMessage(event.newStatus().toString());
            
            // Create SMS notification for status change
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "order_status_changed",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "newStatus", event.newStatus().toString(),
                    "statusMessage", statusMessage
                ),
                NotificationPriority.NORMAL
            );
            
            // Create push notification for status change
            notificationService.createNotification(
                merchantId,
                Recipient.deviceToken("device_token_" + event.orderId(), "Customer"),
                NotificationChannel.PUSH_NOTIFICATION,
                "order_status_changed",
                Map.of(
                    "customerName", "Valued Customer",
                    "orderNumber", event.orderId().toString(),
                    "newStatus", event.newStatus().toString(),
                    "statusMessage", statusMessage
                ),
                NotificationPriority.NORMAL
            );
            
            logger.info("Order status change notifications created successfully for order: {}", event.orderId());
            
        } catch (Exception e) {
            logger.error("Failed to create order status change notifications for order: {}", event.orderId(), e);
        }
    }
    
    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        logger.info("Handling OrderCancelledEvent for order: {}", event.orderId());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromOrderId(event.orderId().toString());
            
            // Create high priority notifications for order cancellation
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "order_status_changed",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "newStatus", "CANCELLED",
                    "statusMessage", "Your order has been cancelled. Reason: " + event.reason()
                ),
                NotificationPriority.HIGH
            );
            
            notificationService.createNotification(
                merchantId,
                Recipient.deviceToken("device_token_" + event.orderId(), "Customer"),
                NotificationChannel.PUSH_NOTIFICATION,
                "order_status_changed",
                Map.of(
                    "customerName", "Valued Customer",
                    "orderNumber", event.orderId().toString(),
                    "newStatus", "CANCELLED",
                    "statusMessage", "Your order has been cancelled. Reason: " + event.reason()
                ),
                NotificationPriority.HIGH
            );
            
            logger.info("Order cancellation notifications created successfully for order: {}", event.orderId());
            
        } catch (Exception e) {
            logger.error("Failed to create order cancellation notifications for order: {}", event.orderId(), e);
        }
    }
    
    private String getStatusMessage(String status) {
        return switch (status) {
            case "CONFIRMED" -> "Your order has been confirmed and is being prepared.";
            case "PREPARING" -> "Your order is being prepared by the restaurant.";
            case "READY_FOR_PICKUP" -> "Your order is ready and waiting for pickup.";
            case "OUT_FOR_DELIVERY" -> "Your order is on its way to you.";
            case "DELIVERED" -> "Your order has been delivered successfully.";
            case "CANCELLED" -> "Your order has been cancelled.";
            default -> "Your order status has been updated.";
        };
    }
}
