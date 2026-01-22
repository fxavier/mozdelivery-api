package com.xavier.mozdeliveryapi.notification.infrastructure;

import org.springframework.context.annotation.Configuration;

import com.xavier.mozdeliveryapi.notification.domain.NotificationRoutingService;

import jakarta.annotation.PostConstruct;

/**
 * Configuration for notification infrastructure components.
 */
@Configuration
public class NotificationInfrastructureConfiguration {
    
    private final NotificationRoutingService routingService;
    private final MockSmsGateway smsGateway;
    private final MockPushNotificationGateway pushNotificationGateway;
    
    public NotificationInfrastructureConfiguration(
            NotificationRoutingService routingService,
            MockSmsGateway smsGateway,
            MockPushNotificationGateway pushNotificationGateway
    ) {
        this.routingService = routingService;
        this.smsGateway = smsGateway;
        this.pushNotificationGateway = pushNotificationGateway;
    }
    
    @PostConstruct
    public void registerGateways() {
        routingService.registerGateway(smsGateway);
        routingService.registerGateway(pushNotificationGateway);
    }
}