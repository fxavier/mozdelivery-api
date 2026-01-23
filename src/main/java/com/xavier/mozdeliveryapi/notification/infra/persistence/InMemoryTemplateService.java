package com.xavier.mozdeliveryapi.notification.infra.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationTemplate;
import com.xavier.mozdeliveryapi.notification.application.usecase.TemplateService;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * In-memory implementation of the template service.
 * This is a temporary implementation for MVP. In production, this would be replaced
 * with a proper database or configuration-based implementation.
 */
@Service
public class InMemoryTemplateService implements TemplateService {
    
    private final Map<String, NotificationTemplate> templates = new ConcurrentHashMap<>();
    
    public InMemoryTemplateService() {
        // Initialize with default templates
        initializeDefaultTemplates();
    }
    
    @Override
    public NotificationTemplate getTemplate(String templateId, NotificationChannel channel) {
        String key = templateId + "_" + channel.name();
        NotificationTemplate template = templates.get(key);
        
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId + " for channel: " + channel);
        }
        
        return template;
    }
    
    @Override
    public void registerTemplate(NotificationTemplate template) {
        String key = template.templateId() + "_" + template.channel().name();
        templates.put(key, template);
    }
    
    private void initializeDefaultTemplates() {
        // Order created templates
        registerTemplate(new NotificationTemplate(
            "order_created",
            "Order Confirmation",
            "Your order {{orderNumber}} has been created successfully. Total: {{totalAmount}} {{currency}}. Estimated delivery: {{estimatedDelivery}}.",
            NotificationChannel.SMS,
            Map.of()
        ));
        
        registerTemplate(new NotificationTemplate(
            "order_created",
            "Order Confirmation - {{orderNumber}}",
            "Hi {{customerName}},\n\nYour order {{orderNumber}} has been created successfully!\n\nOrder Details:\n- Total: {{totalAmount}} {{currency}}\n- Estimated Delivery: {{estimatedDelivery}}\n\nThank you for your order!",
            NotificationChannel.PUSH_NOTIFICATION,
            Map.of()
        ));
        
        // Order status change templates
        registerTemplate(new NotificationTemplate(
            "order_status_changed",
            "Order Update",
            "Your order {{orderNumber}} status has been updated to: {{newStatus}}.",
            NotificationChannel.SMS,
            Map.of()
        ));
        
        registerTemplate(new NotificationTemplate(
            "order_status_changed",
            "Order Update - {{orderNumber}}",
            "Hi {{customerName}},\n\nYour order {{orderNumber}} status has been updated to: {{newStatus}}.\n\n{{statusMessage}}",
            NotificationChannel.PUSH_NOTIFICATION,
            Map.of()
        ));
        
        // Delivery completed templates
        registerTemplate(new NotificationTemplate(
            "delivery_completed",
            "Delivery Completed",
            "Your order {{orderNumber}} has been delivered successfully. Thank you for choosing us!",
            NotificationChannel.SMS,
            Map.of()
        ));
        
        registerTemplate(new NotificationTemplate(
            "delivery_completed",
            "Delivery Completed - {{orderNumber}}",
            "Hi {{customerName}},\n\nGreat news! Your order {{orderNumber}} has been delivered successfully.\n\nDelivered at: {{deliveryTime}}\nDelivered to: {{deliveryAddress}}\n\nThank you for choosing us!",
            NotificationChannel.PUSH_NOTIFICATION,
            Map.of()
        ));
        
        // Critical alerts
        registerTemplate(new NotificationTemplate(
            "critical_alert",
            "Critical Alert",
            "ALERT: {{alertMessage}}. Immediate attention required.",
            NotificationChannel.SMS,
            Map.of()
        ));
        
        registerTemplate(new NotificationTemplate(
            "critical_alert",
            "Critical Alert - {{alertType}}",
            "CRITICAL ALERT\n\n{{alertMessage}}\n\nTime: {{timestamp}}\nSystem: {{systemName}}\n\nImmediate attention required.",
            NotificationChannel.PUSH_NOTIFICATION,
            Map.of()
        ));
    }
}