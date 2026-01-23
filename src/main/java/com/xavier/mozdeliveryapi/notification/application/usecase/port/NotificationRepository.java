package com.xavier.mozdeliveryapi.notification.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;

/**
 * Repository interface for notifications.
 */
public interface NotificationRepository {
    
    /**
     * Save a notification.
     */
    Notification save(Notification notification);
    
    /**
     * Find a notification by its ID.
     */
    Optional<Notification> findById(NotificationId id);
    
    /**
     * Find notifications by tenant ID.
     */
    List<Notification> findByTenantId(TenantId tenantId);
    
    /**
     * Find notifications by status.
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Find notifications by tenant and status.
     */
    List<Notification> findByTenantIdAndStatus(TenantId tenantId, NotificationStatus status);
    
    /**
     * Find notifications by channel.
     */
    List<Notification> findByChannel(NotificationChannel channel);
    
    /**
     * Find pending notifications with high priority.
     */
    List<Notification> findPendingHighPriorityNotifications();
}