package com.xavier.mozdeliveryapi.notification.application.usecase.port;
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationResult;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;

/**
 * Base interface for notification gateways.
 */
public interface NotificationGateway {
    
    /**
     * Send a notification through this gateway.
     * 
     * @param notification the notification to send
     * @return the result of the send operation
     */
    NotificationResult send(Notification notification);
    
    /**
     * Check if this gateway supports the given channel.
     * 
     * @param channel the notification channel
     * @return true if supported, false otherwise
     */
    boolean supports(NotificationChannel channel);
    
    /**
     * Get the priority of this gateway for the given channel.
     * Lower numbers indicate higher priority.
     * 
     * @param channel the notification channel
     * @return the priority value
     */
    int getPriority(NotificationChannel channel);
}