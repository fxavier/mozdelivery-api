package com.xavier.mozdeliveryapi.notification.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.notification.domain.Notification;
import com.xavier.mozdeliveryapi.notification.domain.NotificationGateway;
import com.xavier.mozdeliveryapi.notification.domain.NotificationRoutingService;

/**
 * Implementation of the notification routing service.
 */
@Service
public class NotificationRoutingServiceImpl implements NotificationRoutingService {
    
    private final List<NotificationGateway> gateways = new ArrayList<>();
    
    @Override
    public NotificationGateway routeNotification(Notification notification) {
        return gateways.stream()
            .filter(gateway -> gateway.supports(notification.getChannel()))
            .min(Comparator.comparingInt(gateway -> gateway.getPriority(notification.getChannel())))
            .orElse(null);
    }
    
    @Override
    public void registerGateway(NotificationGateway gateway) {
        gateways.add(gateway);
    }
}