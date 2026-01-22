package com.xavier.mozdeliveryapi.notification.domain;

import java.time.Instant;

import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain event published when a notification is delivered.
 */
public record NotificationDeliveredEvent(
    NotificationId notificationId,
    TenantId tenantId,
    Recipient recipient,
    NotificationChannel channel,
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
        return "NotificationDelivered";
    }
}