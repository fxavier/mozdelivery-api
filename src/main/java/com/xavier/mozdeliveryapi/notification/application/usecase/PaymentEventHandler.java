package com.xavier.mozdeliveryapi.notification.application.usecase;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentCompletedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentFailedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.RefundCompletedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.RefundFailedEvent;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Event handler for payment-related events that trigger notifications.
 */
@Component
public class PaymentEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventHandler.class);
    
    private final NotificationService notificationService;
    private final TenantResolutionService tenantResolutionService;
    
    public PaymentEventHandler(NotificationService notificationService, TenantResolutionService tenantResolutionService) {
        this.notificationService = notificationService;
        this.tenantResolutionService = tenantResolutionService;
    }
    
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Handling PaymentCompletedEvent for payment: {}", event.paymentId());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromOrderId(event.orderId().toString());
            
            // Create notification for successful payment
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "order_status_changed",
                Map.of(
                    "orderNumber", event.orderId().toString(),
                    "newStatus", "PAYMENT_CONFIRMED",
                    "statusMessage", "Payment of " + event.amount().amount() + " " + 
                                   event.amount().currency() + " has been processed successfully."
                ),
                NotificationPriority.NORMAL
            );
            
            logger.info("Payment completion notification created successfully for payment: {}", event.paymentId());
            
        } catch (Exception e) {
            logger.error("Failed to create payment completion notification for payment: {}", event.paymentId(), e);
        }
    }
    
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        logger.info("Handling PaymentFailedEvent for payment: {}", event.paymentId());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromOrderId(event.orderId().toString());
            
            // Create high priority notification for payment failure
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.orderId(), "Customer"),
                NotificationChannel.SMS,
                "critical_alert",
                Map.of(
                    "alertMessage", "Payment failed for order " + event.orderId() + ". Reason: " + event.reason(),
                    "orderNumber", event.orderId().toString()
                ),
                NotificationPriority.HIGH
            );
            
            // Also send push notification
            notificationService.createNotification(
                merchantId,
                Recipient.deviceToken("device_token_" + event.orderId(), "Customer"),
                NotificationChannel.PUSH_NOTIFICATION,
                "critical_alert",
                Map.of(
                    "alertType", "Payment Failed",
                    "alertMessage", "Payment failed for order " + event.orderId() + ". Please try again or use a different payment method.",
                    "timestamp", event.timestamp().toString(),
                    "systemName", "Payment System"
                ),
                NotificationPriority.HIGH
            );
            
            logger.info("Payment failure notifications created successfully for payment: {}", event.paymentId());
            
        } catch (Exception e) {
            logger.error("Failed to create payment failure notifications for payment: {}", event.paymentId(), e);
        }
    }
    
    @EventListener
    public void handleRefundCompleted(RefundCompletedEvent event) {
        logger.info("Handling RefundCompletedEvent for refund: {}", event.refundId());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromPaymentId(event.paymentId().toString());
            
            // Create notification for successful refund
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.paymentId(), "Customer"),
                NotificationChannel.SMS,
                "order_status_changed",
                Map.of(
                    "orderNumber", "Order related to payment " + event.paymentId(),
                    "newStatus", "REFUND_PROCESSED",
                    "statusMessage", "Refund of " + event.amount().amount() + " " + 
                                   event.amount().currency() + " has been processed successfully."
                ),
                NotificationPriority.NORMAL
            );
            
            logger.info("Refund completion notification created successfully for refund: {}", event.refundId());
            
        } catch (Exception e) {
            logger.error("Failed to create refund completion notification for refund: {}", event.refundId(), e);
        }
    }
    
    @EventListener
    public void handleRefundFailed(RefundFailedEvent event) {
        logger.info("Handling RefundFailedEvent for refund: {}", event.refundId());
        
        try {
            MerchantId merchantId = tenantResolutionService.resolveMerchantFromPaymentId(event.paymentId().toString());
            
            // Create critical alert for refund failure
            notificationService.createNotification(
                merchantId,
                Recipient.phone("customer_phone_" + event.paymentId(), "Customer"),
                NotificationChannel.SMS,
                "critical_alert",
                Map.of(
                    "alertMessage", "Refund processing failed. Reason: " + event.reason() + 
                                  ". Please contact customer support.",
                    "orderNumber", "Payment " + event.paymentId()
                ),
                NotificationPriority.CRITICAL
            );
            
            logger.info("Refund failure notification created successfully for refund: {}", event.refundId());
            
        } catch (Exception e) {
            logger.error("Failed to create refund failure notification for refund: {}", event.refundId(), e);
        }
    }
}