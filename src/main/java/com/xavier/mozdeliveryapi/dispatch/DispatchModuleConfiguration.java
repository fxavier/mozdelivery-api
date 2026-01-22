package com.xavier.mozdeliveryapi.dispatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryAssignmentService;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryAssignmentServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryTrackingService;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryTrackingServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.domain.DispatchService;
import com.xavier.mozdeliveryapi.dispatch.domain.DispatchServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.domain.LocationTracker;
import com.xavier.mozdeliveryapi.geospatial.domain.RouteOptimizer;

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