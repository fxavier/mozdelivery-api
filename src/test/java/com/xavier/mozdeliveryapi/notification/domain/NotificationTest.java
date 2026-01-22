package com.xavier.mozdeliveryapi.notification.domain;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Unit tests for the Notification domain model.
 */
class NotificationTest {
    
    @Test
    void shouldCreateNotificationWithCorrectInitialState() {
        // Given
        NotificationId id = NotificationId.generate();
        TenantId tenantId = TenantId.of("550e8400-e29b-41d4-a716-446655440000");
        Recipient recipient = Recipient.phone("+258123456789", "Test Customer");
        NotificationChannel channel = NotificationChannel.SMS;
        String templateId = "order_created";
        String subject = "Order Confirmation";
        String body = "Your order has been created successfully.";
        Map<String, String> parameters = Map.of("orderNumber", "12345");
        NotificationPriority priority = NotificationPriority.NORMAL;
        
        // When
        Notification notification = new Notification(
            id, tenantId, recipient, channel, templateId, 
            subject, body, parameters, priority
        );
        
        // Then
        assertThat(notification.getNotificationId()).isEqualTo(id);
        assertThat(notification.getTenantId()).isEqualTo(tenantId);
        assertThat(notification.getRecipient()).isEqualTo(recipient);
        assertThat(notification.getChannel()).isEqualTo(channel);
        assertThat(notification.getTemplateId()).isEqualTo(templateId);
        assertThat(notification.getSubject()).isEqualTo(subject);
        assertThat(notification.getBody()).isEqualTo(body);
        assertThat(notification.getParameters()).isEqualTo(parameters);
        assertThat(notification.getPriority()).isEqualTo(priority);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getSentAt()).isNull();
        assertThat(notification.getDeliveredAt()).isNull();
    }
    
    @Test
    void shouldMarkNotificationAsSent() {
        // Given
        Notification notification = createTestNotification();
        String externalId = "ext-123";
        
        // When
        notification.markAsSent(externalId);
        
        // Then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getExternalId()).isEqualTo(externalId);
        assertThat(notification.getSentAt()).isNotNull();
    }
    
    @Test
    void shouldMarkNotificationAsDelivered() {
        // Given
        Notification notification = createTestNotification();
        notification.markAsSent("ext-123");
        
        // When
        notification.markAsDelivered();
        
        // Then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DELIVERED);
        assertThat(notification.getDeliveredAt()).isNotNull();
    }
    
    @Test
    void shouldMarkNotificationAsFailed() {
        // Given
        Notification notification = createTestNotification();
        String failureReason = "Invalid phone number";
        
        // When
        notification.markAsFailed(failureReason);
        
        // Then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getFailureReason()).isEqualTo(failureReason);
    }
    
    @Test
    void shouldCancelPendingNotification() {
        // Given
        Notification notification = createTestNotification();
        
        // When
        notification.cancel();
        
        // Then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.CANCELLED);
    }
    
    private Notification createTestNotification() {
        return new Notification(
            NotificationId.generate(),
            TenantId.of("550e8400-e29b-41d4-a716-446655440000"),
            Recipient.phone("+258123456789", "Test Customer"),
            NotificationChannel.SMS,
            "test_template",
            "Test Subject",
            "Test Body",
            Map.of("param1", "value1"),
            NotificationPriority.NORMAL
        );
    }
}