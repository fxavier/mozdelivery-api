package com.xavier.mozdeliveryapi.notification.domain;

/**
 * Gateway interface for push notifications.
 */
public interface PushNotificationGateway extends NotificationGateway {
    
    /**
     * Send a push notification.
     * 
     * @param deviceToken the device token
     * @param title the notification title
     * @param body the notification body
     * @param data additional data payload
     * @return the result of the send operation
     */
    NotificationResult sendPushNotification(String deviceToken, String title, String body, java.util.Map<String, String> data);
}