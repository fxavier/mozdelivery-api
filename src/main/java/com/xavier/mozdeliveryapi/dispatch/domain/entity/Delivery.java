package com.xavier.mozdeliveryapi.dispatch.domain.entity;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryAssignedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryCancelledEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryCompletedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryLocationUpdatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryReassignedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryStatusChangedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryEventType;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Delivery aggregate representing a delivery assignment and its lifecycle.
 */
public class Delivery extends AggregateRoot<DeliveryId> {
    
    private final DeliveryId id;
    private final TenantId tenantId;
    private final OrderId orderId;
    private DeliveryPersonId deliveryPersonId;
    private Route route;
    private DeliveryStatus status;
    private Location currentLocation;
    private Instant estimatedArrival;
    private final List<DeliveryEvent> events;
    private final int orderWeight;
    private final int orderVolume;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new delivery
    public Delivery(DeliveryId id, TenantId tenantId, OrderId orderId, 
                   DeliveryPersonId deliveryPersonId, Route route, 
                   int orderWeight, int orderVolume) {
        this.id = Objects.requireNonNull(id, "Delivery ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.deliveryPersonId = Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        this.route = Objects.requireNonNull(route, "Route cannot be null");
        this.status = DeliveryStatus.ASSIGNED;
        this.currentLocation = route.getStartLocation();
        this.estimatedArrival = Instant.now().plus(route.getEstimatedDuration());
        this.events = new ArrayList<>();
        this.orderWeight = validateWeight(orderWeight);
        this.orderVolume = validateVolume(orderVolume);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Add initial event
        addEvent(DeliveryEvent.of(DeliveryEventType.ASSIGNED, currentLocation, "Delivery assigned"));
        
        registerEvent(DeliveryAssignedEvent.of(id, orderId, deliveryPersonId, tenantId));
    }
    
    // Constructor for reconstituting from persistence
    public Delivery(DeliveryId id, TenantId tenantId, OrderId orderId, 
                   DeliveryPersonId deliveryPersonId, Route route, DeliveryStatus status,
                   Location currentLocation, Instant estimatedArrival, List<DeliveryEvent> events,
                   int orderWeight, int orderVolume, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Delivery ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.deliveryPersonId = Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        this.route = Objects.requireNonNull(route, "Route cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.currentLocation = Objects.requireNonNull(currentLocation, "Current location cannot be null");
        this.estimatedArrival = estimatedArrival;
        this.events = new ArrayList<>(Objects.requireNonNull(events, "Events cannot be null"));
        this.orderWeight = orderWeight;
        this.orderVolume = orderVolume;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected DeliveryId getId() {
        return id;
    }
    
    /**
     * Update delivery status with validation.
     */
    public void updateStatus(DeliveryStatus newStatus, String notes) {
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        if (status == newStatus) {
            return; // No change needed
        }
        
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus));
        }
        
        DeliveryStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = Instant.now();
        
        // Add event for status change
        DeliveryEventType eventType = mapStatusToEventType(newStatus);
        addEvent(DeliveryEvent.of(eventType, currentLocation, notes));
        
        registerEvent(DeliveryStatusChangedEvent.of(id, orderId, oldStatus, newStatus));
        
        // Special handling for completion
        if (newStatus == DeliveryStatus.DELIVERED) {
            registerEvent(DeliveryCompletedEvent.of(id, orderId, deliveryPersonId));
        }
    }
    
    /**
     * Update delivery status without notes.
     */
    public void updateStatus(DeliveryStatus newStatus) {
        updateStatus(newStatus, null);
    }
    
    /**
     * Update current location and recalculate estimated arrival.
     */
    public void updateLocation(Location newLocation) {
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        if (!this.currentLocation.equals(newLocation)) {
            this.currentLocation = newLocation;
            this.updatedAt = Instant.now();
            
            // Recalculate estimated arrival based on distance to destination
            recalculateEstimatedArrival();
            
            addEvent(DeliveryEvent.of(DeliveryEventType.LOCATION_UPDATED, newLocation, 
                    "Location updated"));
            
            registerEvent(DeliveryLocationUpdatedEvent.of(id, newLocation));
        }
    }
    
    /**
     * Reassign delivery to a different delivery person.
     */
    public void reassign(DeliveryPersonId newDeliveryPersonId, Route newRoute) {
        Objects.requireNonNull(newDeliveryPersonId, "New delivery person ID cannot be null");
        Objects.requireNonNull(newRoute, "New route cannot be null");
        
        if (status.isCompleted()) {
            throw new IllegalStateException("Cannot reassign completed delivery");
        }
        
        DeliveryPersonId oldDeliveryPersonId = this.deliveryPersonId;
        this.deliveryPersonId = newDeliveryPersonId;
        this.route = newRoute;
        this.estimatedArrival = Instant.now().plus(newRoute.getEstimatedDuration());
        this.updatedAt = Instant.now();
        
        addEvent(DeliveryEvent.of(DeliveryEventType.REASSIGNED, currentLocation, 
                "Delivery reassigned to new delivery person"));
        
        registerEvent(DeliveryReassignedEvent.of(id, orderId, oldDeliveryPersonId, newDeliveryPersonId));
    }
    
    /**
     * Cancel the delivery.
     */
    public void cancel(String reason) {
        if (status.isCompleted()) {
            throw new IllegalStateException("Cannot cancel completed delivery");
        }
        
        if (!status.canBeCancelled()) {
            throw new IllegalStateException(
                String.format("Cannot cancel delivery in status %s", status));
        }
        
        DeliveryStatus oldStatus = this.status;
        this.status = DeliveryStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        addEvent(DeliveryEvent.of(DeliveryEventType.CANCELLED, currentLocation, reason));
        
        registerEvent(DeliveryStatusChangedEvent.of(id, orderId, oldStatus, DeliveryStatus.CANCELLED));
        registerEvent(DeliveryCancelledEvent.of(id, orderId, reason));
    }
    
    /**
     * Get time remaining until estimated arrival.
     */
    public Duration getTimeToArrival() {
        if (estimatedArrival == null || status.isCompleted()) {
            return Duration.ZERO;
        }
        
        Duration remaining = Duration.between(Instant.now(), estimatedArrival);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }
    
    /**
     * Check if delivery is overdue.
     */
    public boolean isOverdue() {
        return estimatedArrival != null && 
               Instant.now().isAfter(estimatedArrival) && 
               !status.isCompleted();
    }
    
    /**
     * Get delivery progress as percentage (0.0 to 1.0).
     */
    public double getProgress() {
        return switch (status) {
            case ASSIGNED -> 0.0;
            case EN_ROUTE_TO_PICKUP -> 0.2;
            case ARRIVED_AT_PICKUP -> 0.4;
            case IN_TRANSIT -> 0.7;
            case ARRIVED_AT_DELIVERY -> 0.9;
            case DELIVERED -> 1.0;
            case CANCELLED, FAILED -> 0.0;
        };
    }
    
    private void addEvent(DeliveryEvent event) {
        this.events.add(event);
    }
    
    private void recalculateEstimatedArrival() {
        if (status.isCompleted()) {
            return;
        }
        
        // Simple estimation based on distance to destination and average speed
        Location destination = route.getEndLocation();
        double distanceKm = currentLocation.distanceTo(destination).getKilometers().doubleValue();
        double averageSpeedKmh = 30.0; // City driving speed
        
        long estimatedMinutes = Math.round((distanceKm / averageSpeedKmh) * 60);
        this.estimatedArrival = Instant.now().plus(Duration.ofMinutes(estimatedMinutes));
    }
    
    private DeliveryEventType mapStatusToEventType(DeliveryStatus status) {
        return switch (status) {
            case ASSIGNED -> DeliveryEventType.ASSIGNED;
            case EN_ROUTE_TO_PICKUP -> DeliveryEventType.EN_ROUTE_TO_PICKUP;
            case ARRIVED_AT_PICKUP -> DeliveryEventType.ARRIVED_AT_PICKUP;
            case IN_TRANSIT -> DeliveryEventType.PICKED_UP;
            case ARRIVED_AT_DELIVERY -> DeliveryEventType.ARRIVED_AT_DELIVERY;
            case DELIVERED -> DeliveryEventType.DELIVERED;
            case CANCELLED -> DeliveryEventType.CANCELLED;
            case FAILED -> DeliveryEventType.FAILED;
        };
    }
    
    private int validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Order weight cannot be negative");
        }
        return weight;
    }
    
    private int validateVolume(int volume) {
        if (volume < 0) {
            throw new IllegalArgumentException("Order volume cannot be negative");
        }
        return volume;
    }
    
    // Getters
    public DeliveryId getDeliveryId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public OrderId getOrderId() { return orderId; }
    public DeliveryPersonId getDeliveryPersonId() { return deliveryPersonId; }
    public Route getRoute() { return route; }
    public DeliveryStatus getStatus() { return status; }
    public Location getCurrentLocation() { return currentLocation; }
    public Instant getEstimatedArrival() { return estimatedArrival; }
    public List<DeliveryEvent> getEvents() { return Collections.unmodifiableList(events); }
    public int getOrderWeight() { return orderWeight; }
    public int getOrderVolume() { return orderVolume; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}