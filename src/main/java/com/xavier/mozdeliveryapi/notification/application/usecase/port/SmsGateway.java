package com.xavier.mozdeliveryapi.notification.application.usecase.port;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationResult;

/**
 * Gateway interface for SMS notifications.
 */
public interface SmsGateway extends NotificationGateway {
    
    /**
     * Send an SMS message.
     * 
     * @param phoneNumber the recipient phone number
     * @param message the message content
     * @return the result of the send operation
     */
    NotificationResult sendSms(String phoneNumber, String message);
}