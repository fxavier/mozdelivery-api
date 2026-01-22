package com.xavier.mozdeliveryapi.notification.infrastructure;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.notification.domain.Notification;
import com.xavier.mozdeliveryapi.notification.domain.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.NotificationResult;
import com.xavier.mozdeliveryapi.notification.domain.Recipient;
import com.xavier.mozdeliveryapi.notification.domain.SmsGateway;

/**
 * Mock implementation of SMS gateway for development and testing.
 * In production, this would be replaced with actual SMS provider integration.
 */
@Component
public class MockSmsGateway implements SmsGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockSmsGateway.class);
    
    @Override
    public NotificationResult send(Notification notification) {
        if (notification.getRecipient().type() != Recipient.RecipientType.PHONE) {
            return NotificationResult.failure("Invalid recipient type for SMS");
        }
        
        return sendSms(notification.getRecipient().identifier(), notification.getBody());
    }
    
    @Override
    public NotificationResult sendSms(String phoneNumber, String message) {
        // Simulate SMS sending
        logger.info("Sending SMS to {}: {}", phoneNumber, message);
        
        // Simulate some failures for testing
        if (phoneNumber.contains("invalid")) {
            return NotificationResult.failure("Invalid phone number");
        }
        
        // Simulate successful sending
        String externalId = "sms_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("SMS sent successfully with external ID: {}", externalId);
        
        return NotificationResult.success(externalId);
    }
    
    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS;
    }
    
    @Override
    public int getPriority(NotificationChannel channel) {
        return supports(channel) ? 1 : Integer.MAX_VALUE;
    }
}