package com.xavier.mozdeliveryapi.notification.application.dto;

import java.time.Instant;

import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;

/**
 * Response containing notification information.
 */
public record NotificationResponse(
    String id,
    String tenantId,
    Recipient recipient,
    NotificationChannel channel,
    String templateId,
    String subject,
    String body,
    NotificationPriority priority,
    NotificationStatus status,
    String externalId,
    String failureReason,
    Instant createdAt,
    Instant sentAt,
    Instant deliveredAt
) {}