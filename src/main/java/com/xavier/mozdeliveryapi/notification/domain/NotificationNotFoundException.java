package com.xavier.mozdeliveryapi.notification.domain;

/**
 * Exception thrown when a notification is not found.
 */
public class NotificationNotFoundException extends RuntimeException {
    
    public NotificationNotFoundException(NotificationId notificationId) {
        super("Notification not found: " + notificationId);
    }
}