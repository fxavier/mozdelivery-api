package com.xavier.mozdeliveryapi.dispatch.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xavier.mozdeliveryapi.dispatch.application.usecase.DeliveryAssignmentService;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DeliveryAssignmentServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.DeliveryPersonRepository;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.DeliveryRepository;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DeliveryTrackingService;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DeliveryTrackingServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DispatchService;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DispatchServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.LocationTracker;
import com.xavier.mozdeliveryapi.geospatial.application.usecase.port.RouteOptimizer;

/**
 * Configuration for the Dispatch module.
 */
@Configuration
public class DispatchModuleConfiguration {
    
    @Bean
    public DeliveryAssignmentService deliveryAssignmentService(
            DeliveryPersonRepository deliveryPersonRepository,
            DeliveryRepository deliveryRepository) {
        return new DeliveryAssignmentServiceImpl(deliveryPersonRepository, deliveryRepository);
    }
    
    @Bean
    public DispatchService dispatchService(
            DeliveryRepository deliveryRepository,
            DeliveryPersonRepository deliveryPersonRepository,
            DeliveryAssignmentService assignmentService,
            RouteOptimizer routeOptimizer) {
        return new DispatchServiceImpl(
            deliveryRepository,
            deliveryPersonRepository,
            assignmentService,
            routeOptimizer
        );
    }
    
    @Bean
    public DeliveryTrackingService deliveryTrackingService(
            DeliveryRepository deliveryRepository,
            DeliveryPersonRepository deliveryPersonRepository,
            LocationTracker locationTracker) {
        return new DeliveryTrackingServiceImpl(
            deliveryRepository,
            deliveryPersonRepository,
            locationTracker
        );
    }
}
