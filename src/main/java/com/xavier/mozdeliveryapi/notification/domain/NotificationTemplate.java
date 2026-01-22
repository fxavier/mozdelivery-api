package com.xavier.mozdeliveryapi.notification.domain;

import java.util.Map;

/**
 * Value object representing a notification template.
 */
public record NotificationTemplate(
    String templateId,
    String subject,
    String body,
    NotificationChannel channel,
    Map<String, String> defaultParameters
) {
    
    /**
     * Render the template with provided parameters.
     */
    public String renderBody(Map<String, String> parameters) {
        String renderedBody = body;
        
        // Replace default parameters first
        for (Map.Entry<String, String> entry : defaultParameters.entrySet()) {
            renderedBody = renderedBody.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        // Replace provided parameters
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            renderedBody = renderedBody.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return renderedBody;
    }
    
    /**
     * Render the subject with provided parameters.
     */
    public String renderSubject(Map<String, String> parameters) {
        String renderedSubject = subject;
        
        // Replace default parameters first
        for (Map.Entry<String, String> entry : defaultParameters.entrySet()) {
            renderedSubject = renderedSubject.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        // Replace provided parameters
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            renderedSubject = renderedSubject.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return renderedSubject;
    }
}