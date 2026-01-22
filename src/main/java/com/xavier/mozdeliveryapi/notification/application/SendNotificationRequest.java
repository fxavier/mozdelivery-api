package com.xavier.mozdeliveryapi.notification.application;

import java.util.Map;

import com.xavier.mozdeliveryapi.notification.domain.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.Recipient;

/**
 * Request to send a notification.
 */
public record SendNotificationRequest(
    String tenantId,
    Recipient recipient,
    NotificationChannel channel,
    String templateId,
    Map<String, String> parameters,
    NotificationPriority priority
) {}