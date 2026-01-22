package com.xavier.mozdeliveryapi.dispatch.application;

import java.time.Instant;

import com.xavier.mozdeliveryapi.dispatch.domain.LocationHistory;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;

/**
 * Response containing location history information.
 */
public record LocationHistoryResponse(
    Location location,
    Instant timestamp,
    double accuracy,
    double speed,
    String source,
    boolean isRecent,
    boolean hasGoodAccuracy
) {
    
    public static LocationHistoryResponse from(LocationHistory locationHistory) {
        return new LocationHistoryResponse(
            locationHistory.location(),
            locationHistory.timestamp(),
            locationHistory.accuracy(),
            locationHistory.speed(),
            locationHistory.source(),
            locationHistory.isRecent(),
            locationHistory.hasGoodAccuracy()
        );
    }
}