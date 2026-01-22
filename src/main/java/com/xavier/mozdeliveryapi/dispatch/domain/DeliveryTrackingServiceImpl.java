package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.order.domain.OrderId;

/**
 * Implementation of delivery tracking service.
 */
public class DeliveryTrackingServiceImpl implements DeliveryTrackingService {
    
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final LocationTracker locationTracker;
    
    // Default average speed for delivery calculations (km/h)
    private static final double DEFAULT_AVERAGE_SPEED = 30.0;
    private static final double DEFAULT_LOCATION_ACCURACY = 10.0;
    
    public DeliveryTrackingServiceImpl(DeliveryRepository deliveryRepository,
                                      DeliveryPersonRepository deliveryPersonRepository,
                                      LocationTracker locationTracker) {
        this.deliveryRepository = Objects.requireNonNull(deliveryRepository, 
                                                        "Delivery repository cannot be null");
        this.deliveryPersonRepository = Objects.requireNonNull(deliveryPersonRepository, 
                                                              "Delivery person repository cannot be null");
        this.locationTracker = Objects.requireNonNull(locationTracker, 
                                                     "Location tracker cannot be null");
    }
    
    @Override
    public Optional<TrackingUpdate> getTrackingUpdate(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return deliveryRepository.findById(deliveryId)
            .map(TrackingUpdate::from);
    }
    
    @Override
    public Optional<TrackingUpdate> getTrackingUpdateByOrderId(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        return deliveryRepository.findByOrderId(orderId)
            .map(TrackingUpdate::from);
    }
    
    @Override
    public List<TrackingUpdate> getActiveTrackingUpdatesForPerson(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return deliveryRepository.findActiveByDeliveryPersonId(deliveryPersonId)
            .stream()
            .map(TrackingUpdate::from)
            .toList();
    }
    
    @Override
    public TrackingUpdate updateDeliveryLocation(DeliveryId deliveryId, Location newLocation, 
                                                double accuracy, double speed) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        // Update delivery location
        delivery.updateLocation(newLocation);
        
        // Update delivery person location in location tracker
        locationTracker.updateLocation(delivery.getDeliveryPersonId(), newLocation, accuracy, speed);
        
        // Update delivery person entity location
        DeliveryPerson deliveryPerson = deliveryPersonRepository
            .findById(delivery.getDeliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + delivery.getDeliveryPersonId()));
        
        deliveryPerson.updateLocation(newLocation);
        
        // Save both entities
        deliveryRepository.save(delivery);
        deliveryPersonRepository.save(deliveryPerson);
        
        return TrackingUpdate.from(delivery);
    }
    
    @Override
    public TrackingUpdate updateDeliveryLocation(DeliveryId deliveryId, Location newLocation) {
        return updateDeliveryLocation(deliveryId, newLocation, DEFAULT_LOCATION_ACCURACY, 0.0);
    }
    
    @Override
    public Optional<Instant> calculateEstimatedArrival(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return deliveryRepository.findById(deliveryId)
            .map(delivery -> {
                if (delivery.getStatus().isCompleted()) {
                    return null; // No ETA for completed deliveries
                }
                
                Location currentLocation = delivery.getCurrentLocation();
                Location destination = delivery.getRoute().getEndLocation();
                
                return calculateEstimatedArrival(currentLocation, destination, DEFAULT_AVERAGE_SPEED);
            });
    }
    
    @Override
    public Instant calculateEstimatedArrival(Location currentLocation, Location destination, double averageSpeed) {
        Objects.requireNonNull(currentLocation, "Current location cannot be null");
        Objects.requireNonNull(destination, "Destination cannot be null");
        
        if (averageSpeed <= 0) {
            throw new IllegalArgumentException("Average speed must be positive");
        }
        
        Distance distance = currentLocation.distanceTo(destination);
        double distanceKm = distance.getKilometers().doubleValue();
        
        // Calculate travel time in hours
        double travelTimeHours = distanceKm / averageSpeed;
        
        // Convert to minutes and add to current time
        long travelTimeMinutes = Math.round(travelTimeHours * 60);
        
        return Instant.now().plus(Duration.ofMinutes(travelTimeMinutes));
    }
    
    @Override
    public List<TrackingUpdate> getOverdueDeliveryTracking() {
        return deliveryRepository.findOverdueDeliveries(Instant.now())
            .stream()
            .map(TrackingUpdate::from)
            .toList();
    }
    
    @Override
    public Optional<Double> getDeliveryProgress(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return deliveryRepository.findById(deliveryId)
            .map(Delivery::getProgress);
    }
    
    @Override
    public Optional<Duration> getTimeToDelivery(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return deliveryRepository.findById(deliveryId)
            .map(delivery -> {
                if (delivery.getStatus().isCompleted()) {
                    return Duration.ZERO;
                }
                return delivery.getTimeToArrival();
            });
    }
    
    @Override
    public boolean isDeliveryOverdue(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        return deliveryRepository.findById(deliveryId)
            .map(Delivery::isOverdue)
            .orElse(false);
    }
    
    @Override
    public void startDeliveryTracking(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        locationTracker.startTracking(delivery.getDeliveryPersonId());
    }
    
    @Override
    public void stopDeliveryTracking(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        locationTracker.stopTracking(delivery.getDeliveryPersonId());
    }
}