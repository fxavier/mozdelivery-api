package com.xavier.mozdeliveryapi.notification.domain;

import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a notification identifier.
 */
public record NotificationId(UUID value) implements ValueObject {
    
    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID());
    }
    
    public static NotificationId of(String value) {
        return new NotificationId(UUID.fromString(value));
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}