package com.xavier.mozdeliveryapi.notification.application.usecase;

import java.util.List;
import com.xavier.mozdeliveryapi.notification.application.dto.NotificationResponse;
import com.xavier.mozdeliveryapi.notification.application.dto.SendNotificationRequest;

/**
 * Application service for notification operations.
 */
public interface NotificationApplicationService {
    
    /**
     * Send a notification.
     * 
     * @param request the send notification request
     * @return the notification response
     */
    NotificationResponse sendNotification(SendNotificationRequest request);
    
    /**
     * Get a notification by ID.
     * 
     * @param notificationId the notification ID
     * @return the notification response
     */
    NotificationResponse getNotification(String notificationId);
    
    /**
     * Get notifications for a tenant.
     * 
     * @param tenantId the tenant ID
     * @return the list of notification responses
     */
    List<NotificationResponse> getNotificationsForTenant(String tenantId);
    
    /**
     * Cancel a notification.
     * 
     * @param notificationId the notification ID
     */
    void cancelNotification(String notificationId);
    
    /**
     * Process pending notifications.
     * 
     * @return the number of notifications processed
     */
    int processPendingNotifications();
}