package com.xavier.mozdeliveryapi.notification.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.exception.NotificationNotFoundException;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationRepository;
import com.xavier.mozdeliveryapi.notification.application.usecase.NotificationService;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.notification.application.dto.NotificationResponse;
import com.xavier.mozdeliveryapi.notification.application.dto.SendNotificationRequest;

/**
 * Implementation of the notification application service.
 */
@Service
@Transactional
public class NotificationApplicationServiceImpl implements NotificationApplicationService {
    
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    
    public NotificationApplicationServiceImpl(
            NotificationService notificationService,
            NotificationRepository notificationRepository
    ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        TenantId tenantId = TenantId.of(request.tenantId());
        
        // Create the notification
        Notification notification = notificationService.createNotification(
            tenantId,
            request.recipient(),
            request.channel(),
            request.templateId(),
            request.parameters(),
            request.priority()
        );
        
        // Send the notification
        notificationService.sendNotification(notification);
        
        return mapToResponse(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(String notificationId) {
        NotificationId id = NotificationId.of(notificationId);
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        
        if (notificationOpt.isEmpty()) {
            throw new NotificationNotFoundException(id);
        }
        
        return mapToResponse(notificationOpt.get());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForTenant(String tenantId) {
        TenantId id = TenantId.of(tenantId);
        List<Notification> notifications = notificationService.getNotificationsForTenant(id);
        
        return notifications.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    @Override
    public void cancelNotification(String notificationId) {
        NotificationId id = NotificationId.of(notificationId);
        notificationService.cancelNotification(id);
    }
    
    @Override
    public int processPendingNotifications() {
        return notificationService.processPendingNotifications();
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
            notification.getNotificationId().toString(),
            notification.getTenantId().toString(),
            notification.getRecipient(),
            notification.getChannel(),
            notification.getTemplateId(),
            notification.getSubject(),
            notification.getBody(),
            notification.getPriority(),
            notification.getStatus(),
            notification.getExternalId(),
            notification.getFailureReason(),
            notification.getCreatedAt(),
            notification.getSentAt(),
            notification.getDeliveredAt()
        );
    }
}