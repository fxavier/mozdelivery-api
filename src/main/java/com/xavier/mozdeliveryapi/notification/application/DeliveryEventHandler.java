package com.xavier.mozdeliveryapi.notification.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryAssignedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryCompletedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryLocationUpdatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryStatusChangedEvent;
import com.xavier.mozdeliveryapi.notification.domain.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.NotificationService;
import com.xavier.mozdeliveryapi.notification.domain.Recipient;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Event handler for delivery-related events that trigger notifications.
 */
@Component
public class DeliveryEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DeliveryEventHandler.class);
    
    private final NotificationService notificationService;
    private final TenantResolutionService tenantResolutionService;
    
    public DeliveryEventHandler(NotificationService notificationService, TenantResolutionService tenantResolutionService) {
        this.notificationService = notificationService;
        this.tenantResolutionService = tenantResolutionService;
    }
    
    @EventListener
    public void handleDeliveryAssigned(DeliveryAssignedEvent event) {
        logger.info("Handling DeliveryAssignedEvent for delivery: {}", event.deliveryId());
        
        try {
            // Notify customer that delivery has been assigned
            notificationService.createNotification(
                event.tenantId(),
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "order_status_changed",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "newStatus", "OUT_FOR_DELIVERY",
                    "statusMessage", "Your order has been assigned to a delivery person and is on its way."
                ),
                NotificationPriority.NORMAL
            );
            
            // Notify delivery person about new assignment
            notificationService.createNotification(
                event.tenantId(),
                Recipient.deviceToken("delivery_device_" + event.deliveryPersonId(), "Delivery Person"),
                NotificationChannel.PUSH_NOTIFICATION,
                "order_status_changed",
                Map.of(
                    "customerName", "Delivery Person",
                    "orderNumber", event.orderId().toString(),
                    "newStatus", "ASSIGNED",
                    "statusMessage", "You have been assigned a new delivery: " + event.orderId()
                ),
                NotificationPriority.HIGH
            );
            
            logger.info("Delivery assignment notifications created successfully for delivery: {}", event.deliveryId());
            
        } catch (Exception e) {
            logger.error("Failed to create delivery assignment notifications for delivery: {}", event.deliveryId(), e);
        }
    }
    
    @EventListener
    public void handleDeliveryCompleted(DeliveryCompletedEvent event) {
        logger.info("Handling DeliveryCompletedEvent for delivery: {}", event.deliveryId());
        
        try {
            TenantId tenantId = tenantResolutionService.resolveTenantFromOrderId(event.orderId().toString());
            String deliveryTime = LocalDateTime.ofInstant(event.occurredOn(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm"));
            
            // Create SMS notification for delivery completion
            notificationService.createNotification(
                tenantId,
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "delivery_completed",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "deliveryTime", deliveryTime
                ),
                NotificationPriority.NORMAL
            );
            
            // Create push notification for delivery completion
            notificationService.createNotification(
                tenantId,
                Recipient.deviceToken("device_token_" + event.orderId(), "Customer"),
                NotificationChannel.PUSH_NOTIFICATION,
                "delivery_completed",
                Map.of(
                    "customerName", "Valued Customer",
                    "orderNumber", event.orderId().toString(),
                    "deliveryTime", deliveryTime,
                    "deliveryAddress", "Your delivery address"
                ),
                NotificationPriority.NORMAL
            );
            
            logger.info("Delivery completion notifications created successfully for delivery: {}", event.deliveryId());
            
        } catch (Exception e) {
            logger.error("Failed to create delivery completion notifications for delivery: {}", event.deliveryId(), e);
        }
    }
    
    @EventListener
    public void handleDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
        logger.info("Handling DeliveryStatusChangedEvent for delivery: {} - {}", 
                   event.deliveryId(), event.newStatus());
        
        try {
            TenantId tenantId = tenantResolutionService.resolveTenantFromOrderId(event.orderId().toString());
            String statusMessage = getDeliveryStatusMessage(event.newStatus().toString());
            
            // Only send notifications for significant status changes
            if (isSignificantStatusChange(event.newStatus().toString())) {
                notificationService.createNotification(
                    tenantId,
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
                
                logger.info("Delivery status change notification created for delivery: {}", event.deliveryId());
            }
            
        } catch (Exception e) {
            logger.error("Failed to create delivery status change notification for delivery: {}", event.deliveryId(), e);
        }
    }
    
    @EventListener
    public void handleDeliveryLocationUpdated(DeliveryLocationUpdatedEvent event) {
        logger.debug("Handling DeliveryLocationUpdatedEvent for delivery: {}", event.deliveryId());
        
        // Location updates are frequent, so we don't send notifications for every update
        // This could be used for real-time tracking updates in the mobile app
        // For now, we just log it
    }
    
    private String getDeliveryStatusMessage(String status) {
        return switch (status) {
            case "ASSIGNED" -> "Your delivery has been assigned to a delivery person.";
            case "PICKED_UP" -> "Your order has been picked up and is on its way.";
            case "IN_TRANSIT" -> "Your order is currently being delivered.";
            case "DELIVERED" -> "Your order has been delivered successfully.";
            case "FAILED" -> "There was an issue with your delivery. We'll contact you shortly.";
            default -> "Your delivery status has been updated.";
        };
    }
    
    private boolean isSignificantStatusChange(String status) {
        return switch (status) {
            case "ASSIGNED", "PICKED_UP", "DELIVERED", "FAILED" -> true;
            default -> false;
        };
    }
}