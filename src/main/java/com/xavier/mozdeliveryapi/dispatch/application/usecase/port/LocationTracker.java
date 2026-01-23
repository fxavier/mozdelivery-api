package com.xavier.mozdeliveryapi.dispatch.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.LocationHistory;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.TimeRange;

/**
 * Interface for tracking delivery person locations.
 */
public interface LocationTracker {
    
    /**
     * Update the current location of a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param location the new location
     * @param accuracy the location accuracy in meters
     * @param speed the current speed in km/h
     */
    void updateLocation(DeliveryPersonId deliveryPersonId, Location location, double accuracy, double speed);
    
    /**
     * Update the current location of a delivery person with default accuracy.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param location the new location
     */
    void updateLocation(DeliveryPersonId deliveryPersonId, Location location);
    
    /**
     * Get the current location of a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @return the current location, or empty if not available
     */
    Optional<Location> getCurrentLocation(DeliveryPersonId deliveryPersonId);
    
    /**
     * Get the location history for a delivery person within a time range.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param timeRange the time range
     * @return list of location history records
     */
    List<LocationHistory> getLocationHistory(DeliveryPersonId deliveryPersonId, TimeRange timeRange);
    
    /**
     * Get the most recent location history for a delivery person.
     * 
     * @param deliveryPersonId the delivery person ID
     * @param limit maximum number of records to return
     * @return list of recent location history records
     */
    List<LocationHistory> getRecentLocationHistory(DeliveryPersonId deliveryPersonId, int limit);
    
    /**
     * Check if a delivery person's location is being tracked.
     * 
     * @param deliveryPersonId the delivery person ID
     * @return true if location is being tracked
     */
    boolean isLocationTracked(DeliveryPersonId deliveryPersonId);
    
    /**
     * Start tracking a delivery person's location.
     * 
     * @param deliveryPersonId the delivery person ID
     */
    void startTracking(DeliveryPersonId deliveryPersonId);
    
    /**
     * Stop tracking a delivery person's location.
     * 
     * @param deliveryPersonId the delivery person ID
     */
    void stopTracking(DeliveryPersonId deliveryPersonId);
}