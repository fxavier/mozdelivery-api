package com.xavier.mozdeliveryapi.notification.domain;

/**
 * Service for managing notification templates.
 */
public interface TemplateService {
    
    /**
     * Get a notification template by ID and channel.
     * 
     * @param templateId the template ID
     * @param channel the notification channel
     * @return the notification template
     */
    NotificationTemplate getTemplate(String templateId, NotificationChannel channel);
    
    /**
     * Register a new template.
     * 
     * @param template the template to register
     */
    void registerTemplate(NotificationTemplate template);
}