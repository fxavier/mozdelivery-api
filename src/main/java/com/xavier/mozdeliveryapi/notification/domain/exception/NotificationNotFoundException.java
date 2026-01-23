package com.xavier.mozdeliveryapi.notification.domain.exception;
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;

/**
 * Exception thrown when a notification is not found.
 */
public class NotificationNotFoundException extends RuntimeException {
    
    public NotificationNotFoundException(NotificationId notificationId) {
        super("Notification not found: " + notificationId);
    }
}