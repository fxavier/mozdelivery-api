package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DeliveryTrackingService;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.LocationTracker;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.TimeRange;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.application.dto.LocationHistoryResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.TrackingResponse;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * Implementation of tracking application service.
 */
@Service
@Transactional
public class TrackingApplicationServiceImpl implements TrackingApplicationService {
    
    private final DeliveryTrackingService trackingService;
    private final LocationTracker locationTracker;
    
    public TrackingApplicationServiceImpl(DeliveryTrackingService trackingService,
                                         LocationTracker locationTracker) {
        this.trackingService = Objects.requireNonNull(trackingService, "Tracking service cannot be null");
        this.locationTracker = Objects.requireNonNull(locationTracker, "Location tracker cannot be null");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingResponse> getDeliveryTracking(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return trackingService.getTrackingUpdate(deliveryId)
            .map(TrackingResponse::from);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingResponse> getDeliveryTrackingByOrderId(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        return trackingService.getTrackingUpdateByOrderId(orderId)
            .map(TrackingResponse::from);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrackingResponse> getActiveDeliveryTrackingForPerson(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return trackingService.getActiveTrackingUpdatesForPerson(deliveryPersonId)
            .stream()
            .map(TrackingResponse::from)
            .toList();
    }
    
    @Override
    public TrackingResponse updateDeliveryLocation(DeliveryId deliveryId, Location newLocation, 
                                                  double accuracy, double speed) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        return TrackingResponse.from(
            trackingService.updateDeliveryLocation(deliveryId, newLocation, accuracy, speed)
        );
    }
    
    @Override
    public TrackingResponse updateDeliveryLocation(DeliveryId deliveryId, Location newLocation) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        return TrackingResponse.from(
            trackingService.updateDeliveryLocation(deliveryId, newLocation)
        );
    }
    
    @Override
    public void updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, Location newLocation, 
                                            double accuracy, double speed) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        locationTracker.updateLocation(deliveryPersonId, newLocation, accuracy, speed);
    }
    
    @Override
    public void updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, Location newLocation) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        locationTracker.updateLocation(deliveryPersonId, newLocation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Location> getDeliveryPersonCurrentLocation(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return locationTracker.getCurrentLocation(deliveryPersonId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LocationHistoryResponse> getDeliveryPersonLocationHistory(DeliveryPersonId deliveryPersonId, 
                                                                         TimeRange timeRange) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(timeRange, "Time range cannot be null");
        
        return locationTracker.getLocationHistory(deliveryPersonId, timeRange)
            .stream()
            .map(LocationHistoryResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LocationHistoryResponse> getDeliveryPersonRecentLocationHistory(DeliveryPersonId deliveryPersonId, 
                                                                               int limit) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return locationTracker.getRecentLocationHistory(deliveryPersonId, limit)
            .stream()
            .map(LocationHistoryResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> calculateDeliveryEstimatedArrival(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return trackingService.calculateEstimatedArrival(deliveryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Double> getDeliveryProgress(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return trackingService.getDeliveryProgress(deliveryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Duration> getTimeToDelivery(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return trackingService.getTimeToDelivery(deliveryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isDeliveryOverdue(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return trackingService.isDeliveryOverdue(deliveryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TrackingResponse> getOverdueDeliveryTracking() {
        return trackingService.getOverdueDeliveryTracking()
            .stream()
            .map(TrackingResponse::from)
            .toList();
    }
    
    @Override
    public void startDeliveryTracking(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        trackingService.startDeliveryTracking(deliveryId);
    }
    
    @Override
    public void stopDeliveryTracking(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        trackingService.stopDeliveryTracking(deliveryId);
    }
    
    @Override
    public void startDeliveryPersonTracking(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        locationTracker.startTracking(deliveryPersonId);
    }
    
    @Override
    public void stopDeliveryPersonTracking(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        locationTracker.stopTracking(deliveryPersonId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isDeliveryPersonTracked(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return locationTracker.isLocationTracked(deliveryPersonId);
    }
}