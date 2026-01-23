package com.xavier.mozdeliveryapi.notification.application.dto;

import java.util.Map;

import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;

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