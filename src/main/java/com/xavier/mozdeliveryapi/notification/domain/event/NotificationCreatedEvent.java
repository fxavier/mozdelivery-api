package com.xavier.mozdeliveryapi.notification.domain.event;

import java.time.Instant;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;

/**
 * Domain event published when a notification is created.
 */
public record NotificationCreatedEvent(
    NotificationId notificationId,
    TenantId tenantId,
    Recipient recipient,
    NotificationChannel channel,
    NotificationPriority priority,
    Instant timestamp
) implements DomainEvent {
    
    @Override
    public Instant getOccurredOn() {
        return timestamp;
    }
    
    @Override
    public String getAggregateId() {
        return notificationId.toString();
    }
    
    @Override
    public String getEventType() {
        return "NotificationCreated";
    }
}