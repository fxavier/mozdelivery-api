package com.xavier.mozdeliveryapi.notification.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationChannel;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationId;
import com.xavier.mozdeliveryapi.notification.domain.valueobject.NotificationStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

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
     * Find notifications by merchant ID.
     */
    List<Notification> findByMerchantId(MerchantId merchantId);
    
    /**
     * Find notifications by status.
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Find notifications by merchant and status.
     */
    List<Notification> findByMerchantIdAndStatus(MerchantId merchantId, NotificationStatus status);
    
    /**
     * Find notifications by channel.
     */
    List<Notification> findByChannel(NotificationChannel channel);
    
    /**
     * Find pending notifications with high priority.
     */
    List<Notification> findPendingHighPriorityNotifications();
}