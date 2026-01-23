package com.xavier.mozdeliveryapi.notification.infra.persistence;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationRepository;
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationPriority;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;

/**
 * In-memory implementation of the notification repository.
 * This is a temporary implementation for MVP. In production, this would be replaced
 * with a proper database implementation.
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private final Map<NotificationId, Notification> notifications = new ConcurrentHashMap<>();
    
    @Override
    public Notification save(Notification notification) {
        notifications.put(notification.getNotificationId(), notification);
        return notification;
    }
    
    @Override
    public Optional<Notification> findById(NotificationId id) {
        return Optional.ofNullable(notifications.get(id));
    }
    
    @Override
    public List<Notification> findByTenantId(TenantId tenantId) {
        return notifications.values().stream()
            .filter(notification -> notification.getTenantId().equals(tenantId))
            .toList();
    }
    
    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return notifications.values().stream()
            .filter(notification -> notification.getStatus() == status)
            .toList();
    }
    
    @Override
    public List<Notification> findByTenantIdAndStatus(TenantId tenantId, NotificationStatus status) {
        return notifications.values().stream()
            .filter(notification -> notification.getTenantId().equals(tenantId) && 
                                  notification.getStatus() == status)
            .toList();
    }
    
    @Override
    public List<Notification> findByChannel(NotificationChannel channel) {
        return notifications.values().stream()
            .filter(notification -> notification.getChannel() == channel)
            .toList();
    }
    
    @Override
    public List<Notification> findPendingHighPriorityNotifications() {
        return notifications.values().stream()
            .filter(notification -> notification.getStatus() == NotificationStatus.PENDING &&
                                  (notification.getPriority() == NotificationPriority.HIGH ||
                                   notification.getPriority() == NotificationPriority.CRITICAL))
            .toList();
    }
}