package com.xavier.mozdeliveryapi.notification.domain.entity;

import java.time.Instant;
import java.util.Map;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.notification.domain.event.NotificationCancelledEvent;
import com.xavier.mozdeliveryapi.notification.domain.event.NotificationCreatedEvent;
import com.xavier.mozdeliveryapi.notification.domain.event.NotificationDeliveredEvent;
import com.xavier.mozdeliveryapi.notification.domain.event.NotificationFailedEvent;
import com.xavier.mozdeliveryapi.notification.domain.event.NotificationSentEvent;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;

/**
 * Notification aggregate root.
 */
public class Notification extends AggregateRoot<NotificationId> {
    
    private final NotificationId id;
    private final TenantId tenantId;
    private final Recipient recipient;
    private final NotificationChannel channel;
    private final String templateId;
    private final String subject;
    private final String body;
    private final Map<String, String> parameters;
    private final NotificationPriority priority;
    private NotificationStatus status;
    private String externalId;
    private String failureReason;
    private final Instant createdAt;
    private Instant sentAt;
    private Instant deliveredAt;
    
    public Notification(
            NotificationId id,
            TenantId tenantId,
            Recipient recipient,
            NotificationChannel channel,
            String templateId,
            String subject,
            String body,
            Map<String, String> parameters,
            NotificationPriority priority
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.recipient = recipient;
        this.channel = channel;
        this.templateId = templateId;
        this.subject = subject;
        this.body = body;
        this.parameters = parameters;
        this.priority = priority;
        this.status = NotificationStatus.PENDING;
        this.createdAt = Instant.now();
        
        // Publish domain event
        registerEvent(new NotificationCreatedEvent(id, tenantId, recipient, channel, priority, createdAt));
    }
    
    @Override
    protected NotificationId getId() {
        return id;
    }
    
    // Public getter for external access
    public NotificationId getNotificationId() {
        return id;
    }
    
    public void markAsSent(String externalId) {
        this.status = NotificationStatus.SENT;
        this.externalId = externalId;
        this.sentAt = Instant.now();
        
        registerEvent(new NotificationSentEvent(getId(), tenantId, recipient, channel, sentAt));
    }
    
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        
        registerEvent(new NotificationDeliveredEvent(getId(), tenantId, recipient, channel, deliveredAt));
    }
    
    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = reason;
        
        registerEvent(new NotificationFailedEvent(getId(), tenantId, recipient, channel, reason, Instant.now()));
    }
    
    public void cancel() {
        if (status == NotificationStatus.PENDING) {
            this.status = NotificationStatus.CANCELLED;
            registerEvent(new NotificationCancelledEvent(getId(), tenantId, recipient, channel, Instant.now()));
        }
    }
    
    // Getters
    public TenantId getTenantId() { return tenantId; }
    public Recipient getRecipient() { return recipient; }
    public NotificationChannel getChannel() { return channel; }
    public String getTemplateId() { return templateId; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Map<String, String> getParameters() { return parameters; }
    public NotificationPriority getPriority() { return priority; }
    public NotificationStatus getStatus() { return status; }
    public String getExternalId() { return externalId; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
}