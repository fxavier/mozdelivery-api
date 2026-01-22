package com.xavier.mozdeliveryapi.notification.domain;

/**
 * Result of a notification send operation.
 */
public record NotificationResult(
    boolean success,
    String externalId,
    String errorMessage
) {
    
    public static NotificationResult success(String externalId) {
        return new NotificationResult(true, externalId, null);
    }
    
    public static NotificationResult failure(String errorMessage) {
        return new NotificationResult(false, null, errorMessage);
    }
}