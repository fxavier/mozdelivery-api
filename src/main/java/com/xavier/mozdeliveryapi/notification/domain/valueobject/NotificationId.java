package com.xavier.mozdeliveryapi.notification.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.UUID;


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