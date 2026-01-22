package com.xavier.mozdeliveryapi.notification.infrastructure;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.notification.domain.Notification;
import com.xavier.mozdeliveryapi.notification.domain.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.NotificationResult;
import com.xavier.mozdeliveryapi.notification.domain.PushNotificationGateway;
import com.xavier.mozdeliveryapi.notification.domain.Recipient;

/**
 * Mock implementation of push notification gateway for development and testing.
 * In production, this would be replaced with actual push notification service integration (FCM, APNS).
 */
@Component
public class MockPushNotificationGateway implements PushNotificationGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockPushNotificationGateway.class);
    
    @Override
    public NotificationResult send(Notification notification) {
        if (notification.getRecipient().type() != Recipient.RecipientType.DEVICE_TOKEN) {
            return NotificationResult.failure("Invalid recipient type for push notification");
        }
        
        return sendPushNotification(
            notification.getRecipient().identifier(),
            notification.getSubject(),
            notification.getBody(),
            notification.getParameters()
        );
    }
    
    @Override
    public NotificationResult sendPushNotification(String deviceToken, String title, String body, Map<String, String> data) {
        // Simulate push notification sending
        logger.info("Sending push notification to device {}: {} - {}", deviceToken, title, body);
        if (data != null && !data.isEmpty()) {
            logger.info("Push notification data: {}", data);
        }
        
        // Simulate some failures for testing
        if (deviceToken.contains("invalid")) {
            return NotificationResult.failure("Invalid device token");
        }
        
        // Simulate successful sending
        String externalId = "push_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("Push notification sent successfully with external ID: {}", externalId);
        
        return NotificationResult.success(externalId);
    }
    
    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.PUSH_NOTIFICATION;
    }
    
    @Override
    public int getPriority(NotificationChannel channel) {
        return supports(channel) ? 1 : Integer.MAX_VALUE;
    }
}