package com.xavier.mozdeliveryapi.notification.infra.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.notification.application.usecase.NotificationService;

/**
 * Service for processing pending notifications.
 */
@Service
public class NotificationProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessingService.class);
    
    private final NotificationService notificationService;
    
    public NotificationProcessingService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Process pending notifications every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000)
    public void processPendingNotifications() {
        try {
            int processed = notificationService.processPendingNotifications();
            if (processed > 0) {
                logger.info("Processed {} pending notifications", processed);
            }
        } catch (Exception e) {
            logger.error("Error processing pending notifications", e);
        }
    }
    
    /**
     * Process high priority notifications more frequently (every 10 seconds).
     */
    @Scheduled(fixedDelay = 10000)
    public void processHighPriorityNotifications() {
        try {
            // This would be implemented to specifically handle high priority notifications
            // For now, the regular processing handles all notifications
            logger.debug("High priority notification processing check completed");
        } catch (Exception e) {
            logger.error("Error processing high priority notifications", e);
        }
    }
}