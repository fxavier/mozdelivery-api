package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.order.domain.OrderId;

/**
 * Domain service for delivery tracking operations.
 */
public interface DeliveryTrackingService {
    
    /**
     * Get real-time tracking information for a delivery.
     * 
     * @param deliveryId the delivery ID
     * @return tracking update, or empty if delivery not found
     */
    Optional<TrackingUpdate> getTrackingUpdate(DeliveryId deliveryId);
    
    /**
     * Get real-time tracking information for a delivery by order ID.
     * 
     * @param orderId the order ID
     * @return tracking update, or empty if delivery not found
     */
    Optional<TrackingUpdate> getTrackingUpdateByOrderId(OrderId orderId);
    
    /**
     * Get tracking updates for all active deliveries of a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @return list of tracking updates
     */
    List<TrackingUpdate> getActiveTrackingUpdatesForPerson(DeliveryPersonId deliveryPersonId);
    
    /**
     * Update delivery location and recalculate estimated arrival time.
     * 
     * @param deliveryId the delivery ID
     * @param newLocation the new location
     * @param accuracy the location accuracy in meters
     * @param speed the current speed in km/h
     * @return updated tracking information
     */
    TrackingUpdate updateDeliveryLocation(DeliveryId deliveryId, Location newLocation, 
                                         double accuracy, double speed);
    
    /**
     * Update delivery location with default accuracy.
     * 
     * @param deliveryId the delivery ID
     * @param newLocation the new location
     * @return updated tracking information
     */
    TrackingUpdate updateDeliveryLocation(DeliveryId deliveryId, Location newLocation);
    
    /**
     * Calculate estimated arrival time for a delivery based on current location and route.
     * 
     * @param deliveryId the delivery ID
     * @return estimated arrival time, or empty if cannot be calculated
     */
    Optional<Instant> calculateEstimatedArrival(DeliveryId deliveryId);
    
    /**
     * Calculate estimated arrival time based on current location and destination.
     * 
     * @param currentLocation the current location
     * @param destination the destination location
     * @param averageSpeed the average speed in km/h
     * @return estimated arrival time
     */
    Instant calculateEstimatedArrival(Location currentLocation, Location destination, double averageSpeed);
    
    /**
     * Get all overdue deliveries with their tracking information.
     * 
     * @return list of tracking updates for overdue deliveries
     */
    List<TrackingUpdate> getOverdueDeliveryTracking();
    
    /**
     * Get delivery progress as a percentage (0.0 to 1.0).
     * 
     * @param deliveryId the delivery ID
     * @return progress percentage, or empty if delivery not found
     */
    Optional<Double> getDeliveryProgress(DeliveryId deliveryId);
    
    /**
     * Get time remaining until estimated delivery.
     * 
     * @param deliveryId the delivery ID
     * @return time remaining, or empty if delivery not found or completed
     */
    Optional<Duration> getTimeToDelivery(DeliveryId deliveryId);
    
    /**
     * Check if a delivery is overdue.
     * 
     * @param deliveryId the delivery ID
     * @return true if delivery is overdue
     */
    boolean isDeliveryOverdue(DeliveryId deliveryId);
    
    /**
     * Start real-time tracking for a delivery.
     * 
     * @param deliveryId the delivery ID
     */
    void startDeliveryTracking(DeliveryId deliveryId);
    
    /**
     * Stop real-time tracking for a delivery.
     * 
     * @param deliveryId the delivery ID
     */
    void stopDeliveryTracking(DeliveryId deliveryId);
}