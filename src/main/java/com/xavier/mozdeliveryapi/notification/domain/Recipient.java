package com.xavier.mozdeliveryapi.notification.domain;

/**
 * Value object representing a notification recipient.
 */
public record Recipient(
    String identifier,
    RecipientType type,
    String displayName
) {
    
    public static Recipient phone(String phoneNumber, String displayName) {
        return new Recipient(phoneNumber, RecipientType.PHONE, displayName);
    }
    
    public static Recipient email(String emailAddress, String displayName) {
        return new Recipient(emailAddress, RecipientType.EMAIL, displayName);
    }
    
    public static Recipient deviceToken(String token, String displayName) {
        return new Recipient(token, RecipientType.DEVICE_TOKEN, displayName);
    }
    
    public static Recipient userId(String userId, String displayName) {
        return new Recipient(userId, RecipientType.USER_ID, displayName);
    }
    
    public enum RecipientType {
        PHONE,
        EMAIL,
        DEVICE_TOKEN,
        USER_ID
    }
}