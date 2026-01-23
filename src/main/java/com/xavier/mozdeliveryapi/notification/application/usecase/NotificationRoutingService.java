package com.xavier.mozdeliveryapi.notification.application.usecase;

import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationGateway;

/**
 * Service for routing notifications to appropriate gateways.
 */
public interface NotificationRoutingService {
    
    /**
     * Route a notification to the appropriate gateway.
     * 
     * @param notification the notification to route
     * @return the gateway to use, or null if none available
     */
    NotificationGateway routeNotification(Notification notification);
    
    /**
     * Register a notification gateway.
     * 
     * @param gateway the gateway to register
     */
    void registerGateway(NotificationGateway gateway);
}
