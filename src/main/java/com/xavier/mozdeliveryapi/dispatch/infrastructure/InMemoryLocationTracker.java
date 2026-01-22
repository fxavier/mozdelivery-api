package com.xavier.mozdeliveryapi.dispatch.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.LocationHistory;
import com.xavier.mozdeliveryapi.dispatch.domain.LocationTracker;
import com.xavier.mozdeliveryapi.dispatch.domain.TimeRange;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;

/**
 * In-memory location tracker for development and testing.
 */
@Component
public class InMemoryLocationTracker implements LocationTracker {

    private final Map<DeliveryPersonId, List<LocationHistory>> history = new ConcurrentHashMap<>();
    private final Set<DeliveryPersonId> tracked = ConcurrentHashMap.newKeySet();

    @Override
    public void updateLocation(DeliveryPersonId deliveryPersonId, Location location, double accuracy, double speed) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        if (accuracy < 0) {
            throw new IllegalArgumentException("Accuracy cannot be negative");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }

        startTracking(deliveryPersonId);
        LocationHistory record = LocationHistory.of(location, Instant.now(), accuracy, speed);
        history.computeIfAbsent(deliveryPersonId, id -> new ArrayList<>()).add(record);
    }

    @Override
    public void updateLocation(DeliveryPersonId deliveryPersonId, Location location) {
        updateLocation(deliveryPersonId, location, 50.0, 0.0);
    }

    @Override
    public Optional<Location> getCurrentLocation(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        List<LocationHistory> records = history.get(deliveryPersonId);
        if (records == null || records.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(records.get(records.size() - 1).location());
    }

    @Override
    public List<LocationHistory> getLocationHistory(DeliveryPersonId deliveryPersonId, TimeRange timeRange) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(timeRange, "Time range cannot be null");
        List<LocationHistory> records = history.get(deliveryPersonId);
        if (records == null || records.isEmpty()) {
            return List.of();
        }

        return records.stream()
                .filter(record -> timeRange.contains(record.timestamp()))
                .toList();
    }

    @Override
    public List<LocationHistory> getRecentLocationHistory(DeliveryPersonId deliveryPersonId, int limit) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        if (limit <= 0) {
            return List.of();
        }
        List<LocationHistory> records = history.get(deliveryPersonId);
        if (records == null || records.isEmpty()) {
            return List.of();
        }

        int start = Math.max(records.size() - limit, 0);
        return records.subList(start, records.size());
    }

    @Override
    public boolean isLocationTracked(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return tracked.contains(deliveryPersonId);
    }

    @Override
    public void startTracking(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        tracked.add(deliveryPersonId);
    }

    @Override
    public void stopTracking(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        tracked.remove(deliveryPersonId);
    }
}
