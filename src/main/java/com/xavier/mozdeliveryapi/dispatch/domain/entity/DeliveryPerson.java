package com.xavier.mozdeliveryapi.dispatch.domain.entity;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;

import java.time.Instant;
import java.util.Objects;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonCreatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonLocationUpdatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonStatusChangedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * DeliveryPerson aggregate representing a person who delivers orders.
 */
public class DeliveryPerson extends AggregateRoot<DeliveryPersonId> {
    
    private final DeliveryPersonId id;
    private final TenantId tenantId;
    private final String name;
    private final String phoneNumber;
    private final String vehicleType;
    private final DeliveryCapacity capacity;
    private DeliveryPersonStatus status;
    private Location currentLocation;
    private int currentOrderCount;
    private int currentWeight;
    private int currentVolume;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new delivery person
    public DeliveryPerson(DeliveryPersonId id, TenantId tenantId, String name, 
                         String phoneNumber, String vehicleType, DeliveryCapacity capacity,
                         Location initialLocation) {
        this.id = Objects.requireNonNull(id, "Delivery person ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null").trim();
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null").trim();
        this.capacity = Objects.requireNonNull(capacity, "Capacity cannot be null");
        this.currentLocation = Objects.requireNonNull(initialLocation, "Initial location cannot be null");
        this.status = DeliveryPersonStatus.AVAILABLE;
        this.currentOrderCount = 0;
        this.currentWeight = 0;
        this.currentVolume = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        if (vehicleType.isBlank()) {
            throw new IllegalArgumentException("Vehicle type cannot be blank");
        }
        
        registerEvent(DeliveryPersonCreatedEvent.of(id, tenantId, name));
    }
    
    // Constructor for reconstituting from persistence
    public DeliveryPerson(DeliveryPersonId id, TenantId tenantId, String name, 
                         String phoneNumber, String vehicleType, DeliveryCapacity capacity,
                         DeliveryPersonStatus status, Location currentLocation,
                         int currentOrderCount, int currentWeight, int currentVolume,
                         Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Delivery person ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        this.capacity = Objects.requireNonNull(capacity, "Capacity cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.currentLocation = Objects.requireNonNull(currentLocation, "Current location cannot be null");
        this.currentOrderCount = currentOrderCount;
        this.currentWeight = currentWeight;
        this.currentVolume = currentVolume;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected DeliveryPersonId getId() {
        return id;
    }
    
    /**
     * Update the delivery person's status.
     */
    public void updateStatus(DeliveryPersonStatus newStatus) {
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        if (this.status == newStatus) {
            return; // No change needed
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus));
        }
        
        DeliveryPersonStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = Instant.now();
        
        registerEvent(DeliveryPersonStatusChangedEvent.of(id, oldStatus, newStatus));
    }
    
    /**
     * Update the delivery person's current location.
     */
    public void updateLocation(Location newLocation) {
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        if (!this.currentLocation.equals(newLocation)) {
            this.currentLocation = newLocation;
            this.updatedAt = Instant.now();
            
            registerEvent(DeliveryPersonLocationUpdatedEvent.of(id, newLocation));
        }
    }
    
    /**
     * Check if the delivery person can accept a new delivery.
     */
    public boolean canAcceptDelivery(int orderWeight, int orderVolume) {
        if (!status.isAvailable()) {
            return false;
        }
        
        return capacity.canAccommodate(
            currentOrderCount + 1,
            currentWeight + orderWeight,
            currentVolume + orderVolume
        );
    }
    
    /**
     * Assign a delivery to this person.
     */
    public void assignDelivery(int orderWeight, int orderVolume) {
        if (!canAcceptDelivery(orderWeight, orderVolume)) {
            throw new IllegalStateException("Cannot accept delivery - capacity exceeded or not available");
        }
        
        this.currentOrderCount++;
        this.currentWeight += orderWeight;
        this.currentVolume += orderVolume;
        this.updatedAt = Instant.now();
        
        // If at capacity, mark as busy
        if (!capacity.canAccommodate(currentOrderCount + 1, currentWeight, currentVolume)) {
            updateStatus(DeliveryPersonStatus.BUSY);
        }
    }
    
    /**
     * Complete a delivery and free up capacity.
     */
    public void completeDelivery(int orderWeight, int orderVolume) {
        if (currentOrderCount <= 0) {
            throw new IllegalStateException("No active deliveries to complete");
        }
        
        this.currentOrderCount = Math.max(0, currentOrderCount - 1);
        this.currentWeight = Math.max(0, currentWeight - orderWeight);
        this.currentVolume = Math.max(0, currentVolume - orderVolume);
        this.updatedAt = Instant.now();
        
        // If was busy and now has capacity, mark as available
        if (status == DeliveryPersonStatus.BUSY && 
            capacity.canAccommodate(currentOrderCount + 1, currentWeight, currentVolume)) {
            updateStatus(DeliveryPersonStatus.AVAILABLE);
        }
    }
    
    /**
     * Get current capacity utilization percentage.
     */
    public double getCapacityUtilization() {
        return capacity.getUtilizationPercentage(currentOrderCount, currentWeight, currentVolume);
    }
    
    /**
     * Get remaining capacity.
     */
    public DeliveryCapacity getRemainingCapacity() {
        return capacity.subtract(currentOrderCount, currentWeight, currentVolume);
    }
    
    /**
     * Check if delivery person is available for assignment.
     */
    public boolean isAvailable() {
        return status.isAvailable();
    }
    
    // Getters
    public DeliveryPersonId getDeliveryPersonId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getVehicleType() { return vehicleType; }
    public DeliveryCapacity getCapacity() { return capacity; }
    public DeliveryPersonStatus getStatus() { return status; }
    public Location getCurrentLocation() { return currentLocation; }
    public int getCurrentOrderCount() { return currentOrderCount; }
    public int getCurrentWeight() { return currentWeight; }
    public int getCurrentVolume() { return currentVolume; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}