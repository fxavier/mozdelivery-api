package com.xavier.mozdeliveryapi.notification.infra.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;
import com.xavier.mozdeliveryapi.notification.application.usecase.port.NotificationGateway;
import com.xavier.mozdeliveryapi.notification.application.usecase.NotificationRoutingService;

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