package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.TimeRange;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.application.dto.LocationHistoryResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.TrackingResponse;

/**
 * Application service for delivery tracking operations.
 */
public interface TrackingApplicationService {
    
    /**
     * Get real-time tracking information for a delivery.
     */
    Optional<TrackingResponse> getDeliveryTracking(DeliveryId deliveryId);
    
    /**
     * Get real-time tracking information for a delivery by order ID.
     */
    Optional<TrackingResponse> getDeliveryTrackingByOrderId(OrderId orderId);
    
    /**
     * Get tracking information for all active deliveries of a delivery person.
     */
    List<TrackingResponse> getActiveDeliveryTrackingForPerson(DeliveryPersonId deliveryPersonId);
    
    /**
     * Update delivery location with accuracy and speed information.
     */
    TrackingResponse updateDeliveryLocation(DeliveryId deliveryId, Location newLocation, 
                                           double accuracy, double speed);
    
    /**
     * Update delivery location with default accuracy.
     */
    TrackingResponse updateDeliveryLocation(DeliveryId deliveryId, Location newLocation);
    
    /**
     * Update delivery person location.
     */
    void updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, Location newLocation, 
                                     double accuracy, double speed);
    
    /**
     * Update delivery person location with default accuracy.
     */
    void updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, Location newLocation);
    
    /**
     * Get current location of a delivery person.
     */
    Optional<Location> getDeliveryPersonCurrentLocation(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get location history for a delivery person.
     */
    List<LocationHistoryResponse> getDeliveryPersonLocationHistory(DeliveryPersonId deliveryPersonId, 
                                                                  TimeRange timeRange);
    
    /**
     * Get recent location history for a delivery person.
     */
    List<LocationHistoryResponse> getDeliveryPersonRecentLocationHistory(DeliveryPersonId deliveryPersonId, 
                                                                        int limit);
    
    /**
     * Calculate estimated arrival time for a delivery.
     */
    Optional<Instant> calculateDeliveryEstimatedArrival(DeliveryId deliveryId);
    
    /**
     * Get delivery progress as percentage.
     */
    Optional<Double> getDeliveryProgress(DeliveryId deliveryId);
    
    /**
     * Get time remaining until delivery.
     */
    Optional<Duration> getTimeToDelivery(DeliveryId deliveryId);
    
    /**
     * Check if delivery is overdue.
     */
    boolean isDeliveryOverdue(DeliveryId deliveryId);
    
    /**
     * Get all overdue deliveries with tracking information.
     */
    List<TrackingResponse> getOverdueDeliveryTracking();
    
    /**
     * Start real-time tracking for a delivery.
     */
    void startDeliveryTracking(DeliveryId deliveryId);
    
    /**
     * Stop real-time tracking for a delivery.
     */
    void stopDeliveryTracking(DeliveryId deliveryId);
    
    /**
     * Start location tracking for a delivery person.
     */
    void startDeliveryPersonTracking(DeliveryPersonId deliveryPersonId);
    
    /**
     * Stop location tracking for a delivery person.
     */
    void stopDeliveryPersonTracking(DeliveryPersonId deliveryPersonId);
    
    /**
     * Check if delivery person location is being tracked.
     */
    boolean isDeliveryPersonTracked(DeliveryPersonId deliveryPersonId);
}