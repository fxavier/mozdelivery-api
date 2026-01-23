package com.xavier.mozdeliveryapi.notification.application.usecase;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationResult;
import com.xavier.mozdeliveryapi.notification.domain.entity.NotificationTemplate;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.Recipient;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationRepository;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationGateway;

/**
 * Implementation of the notification domain service.
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final TemplateService templateService;
    private final NotificationRoutingService routingService;
    
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            TemplateService templateService,
            NotificationRoutingService routingService
    ) {
        this.notificationRepository = notificationRepository;
        this.templateService = templateService;
        this.routingService = routingService;
    }
    
    @Override
    public Notification createNotification(
            TenantId tenantId,
            Recipient recipient,
            NotificationChannel channel,
            String templateId,
            Map<String, String> parameters,
            NotificationPriority priority
    ) {
        // Get the template
        NotificationTemplate template = templateService.getTemplate(templateId, channel);
        
        // Render the template
        String subject = template.renderSubject(parameters);
        String body = template.renderBody(parameters);
        
        // Create the notification
        Notification notification = new Notification(
            NotificationId.generate(),
            tenantId,
            recipient,
            channel,
            templateId,
            subject,
            body,
            parameters,
            priority
        );
        
        return notificationRepository.save(notification);
    }
    
    @Override
    public NotificationResult sendNotification(Notification notification) {
        // Route the notification to the appropriate gateway
        NotificationGateway gateway = routingService.routeNotification(notification);
        
        if (gateway == null) {
            notification.markAsFailed("No gateway available for channel: " + notification.getChannel());
            notificationRepository.save(notification);
            return NotificationResult.failure("No gateway available");
        }
        
        // Send the notification
        NotificationResult result = gateway.send(notification);
        
        // Update the notification status
        if (result.success()) {
            notification.markAsSent(result.externalId());
        } else {
            notification.markAsFailed(result.errorMessage());
        }
        
        notificationRepository.save(notification);
        return result;
    }
    
    @Override
    public int processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository.findByStatus(NotificationStatus.PENDING);
        int processed = 0;
        
        for (Notification notification : pendingNotifications) {
            try {
                sendNotification(notification);
                processed++;
            } catch (Exception e) {
                notification.markAsFailed("Processing error: " + e.getMessage());
                notificationRepository.save(notification);
            }
        }
        
        return processed;
    }
    
    @Override
    public List<Notification> getNotificationsForTenant(TenantId tenantId) {
        return notificationRepository.findByTenantId(tenantId);
    }
    
    @Override
    public void cancelNotification(NotificationId notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.cancel();
            notificationRepository.save(notification);
        }
    }
}
