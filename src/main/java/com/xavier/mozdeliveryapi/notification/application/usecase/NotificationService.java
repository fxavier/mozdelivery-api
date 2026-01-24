package com.xavier.mozdeliveryapi.notification.application.usecase;

import java.util.List;
import java.util.Map;

import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationResult;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain service for notification operations.
 */
public interface NotificationService {
    
    /**
     * Create a new notification.
     * 
     * @param merchantId the merchant ID
     * @param recipient the notification recipient
     * @param channel the notification channel
     * @param templateId the template ID
     * @param parameters the template parameters
     * @param priority the notification priority
     * @return the created notification
     */
    Notification createNotification(
        MerchantId merchantId,
        Recipient recipient,
        NotificationChannel channel,
        String templateId,
        Map<String, String> parameters,
        NotificationPriority priority
    );
    
    /**
     * Send a notification.
     * 
     * @param notification the notification to send
     * @return the result of the send operation
     */
    NotificationResult sendNotification(Notification notification);
    
    /**
     * Process pending notifications.
     * 
     * @return the number of notifications processed
     */
    int processPendingNotifications();
    
    /**
     * Get notifications for a merchant.
     * 
     * @param merchantId the merchant ID
     * @return the list of notifications
     */
    List<Notification> getNotificationsForMerchant(MerchantId merchantId);
    
    /**
     * Cancel a notification.
     * 
     * @param notificationId the notification ID
     */
    void cancelNotification(NotificationId notificationId);
}