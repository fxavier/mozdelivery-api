package com.xavier.mozdeliveryapi.notification.application;

import java.time.Instant;

import com.xavier.mozdeliveryapi.notification.domain.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.NotificationStatus;
import com.xavier.mozdeliveryapi.notification.domain.Recipient;

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