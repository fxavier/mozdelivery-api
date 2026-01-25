package com.xavier.mozdeliveryapi.dispatch.domain.entity;

import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonCreatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonLocationUpdatedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.event.DeliveryPersonStatusChangedEvent;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.AvailabilitySchedule;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.VehicleInfo;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import java.time.Instant;
import java.util.Objects;

/**
 * Enhanced courier profile aggregate that extends delivery person with registration and approval workflow.
 */
public class CourierProfile extends AggregateRoot<DeliveryPersonId> {
    
    private final DeliveryPersonId id;
    private final UserId userId;
    private final TenantId tenantId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private VehicleInfo vehicleInfo;
    private DeliveryCapacity capacity;
    private DeliveryPersonStatus status;
    private CourierApprovalStatus approvalStatus;
    private Location currentLocation;
    private AvailabilitySchedule availabilitySchedule;
    private final String city;
    private String drivingLicenseNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String notes;
    private String reviewNotes;
    private String reviewerComments;
    private int currentOrderCount;
    private int currentWeight;
    private int currentVolume;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant approvedAt;
    
    // Constructor for new courier registration
    public CourierProfile(DeliveryPersonId id, UserId userId, TenantId tenantId,
                         String firstName, String lastName, String email, String phoneNumber,
                         VehicleInfo vehicleInfo, DeliveryCapacity capacity, Location initialLocation,
                         AvailabilitySchedule availabilitySchedule, String city,
                         String drivingLicenseNumber, String emergencyContactName,
                         String emergencyContactPhone, String notes) {
        this.id = Objects.requireNonNull(id, "Delivery person ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
        this.email = validateEmail(email);
        this.phoneNumber = validatePhoneNumber(phoneNumber);
        this.vehicleInfo = Objects.requireNonNull(vehicleInfo, "Vehicle info cannot be null");
        this.capacity = Objects.requireNonNull(capacity, "Capacity cannot be null");
        this.currentLocation = Objects.requireNonNull(initialLocation, "Initial location cannot be null");
        this.availabilitySchedule = Objects.requireNonNull(availabilitySchedule, "Availability schedule cannot be null");
        this.city = validateCity(city);
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.notes = notes;
        
        // Initial status for new registrations
        this.status = DeliveryPersonStatus.INACTIVE;
        this.approvalStatus = CourierApprovalStatus.PENDING;
        this.currentOrderCount = 0;
        this.currentWeight = 0;
        this.currentVolume = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        registerEvent(DeliveryPersonCreatedEvent.of(id, tenantId, getFullName()));
    }
    
    // Constructor for reconstituting from persistence
    public CourierProfile(DeliveryPersonId id, UserId userId, TenantId tenantId,
                         String firstName, String lastName, String email, String phoneNumber,
                         VehicleInfo vehicleInfo, DeliveryCapacity capacity,
                         DeliveryPersonStatus status, CourierApprovalStatus approvalStatus,
                         Location currentLocation, AvailabilitySchedule availabilitySchedule,
                         String city, String drivingLicenseNumber, String emergencyContactName,
                         String emergencyContactPhone, String notes, String reviewNotes,
                         String reviewerComments, int currentOrderCount, int currentWeight,
                         int currentVolume, Instant createdAt, Instant updatedAt, Instant approvedAt) {
        this.id = Objects.requireNonNull(id, "Delivery person ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleInfo = vehicleInfo;
        this.capacity = capacity;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.currentLocation = currentLocation;
        this.availabilitySchedule = availabilitySchedule;
        this.city = city;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.notes = notes;
        this.reviewNotes = reviewNotes;
        this.reviewerComments = reviewerComments;
        this.currentOrderCount = currentOrderCount;
        this.currentWeight = currentWeight;
        this.currentVolume = currentVolume;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
    }
    
    @Override
    protected DeliveryPersonId getId() {
        return id;
    }
    
    /**
     * Approve courier registration.
     */
    public void approve(String reviewNotes, String reviewerComments) {
        if (!approvalStatus.canTransitionTo(CourierApprovalStatus.APPROVED)) {
            throw new IllegalStateException("Cannot approve courier in status: " + approvalStatus);
        }
        
        this.approvalStatus = CourierApprovalStatus.APPROVED;
        this.status = DeliveryPersonStatus.OFF_DUTY; // Ready to go on duty
        this.reviewNotes = reviewNotes;
        this.reviewerComments = reviewerComments;
        this.approvedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Reject courier registration.
     */
    public void reject(String reviewNotes, String reviewerComments) {
        if (!approvalStatus.canTransitionTo(CourierApprovalStatus.REJECTED)) {
            throw new IllegalStateException("Cannot reject courier in status: " + approvalStatus);
        }
        
        this.approvalStatus = CourierApprovalStatus.REJECTED;
        this.status = DeliveryPersonStatus.INACTIVE;
        this.reviewNotes = reviewNotes;
        this.reviewerComments = reviewerComments;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Suspend courier account.
     */
    public void suspend(String reason) {
        if (!approvalStatus.canTransitionTo(CourierApprovalStatus.SUSPENDED)) {
            throw new IllegalStateException("Cannot suspend courier in status: " + approvalStatus);
        }
        
        this.approvalStatus = CourierApprovalStatus.SUSPENDED;
        this.status = DeliveryPersonStatus.INACTIVE;
        this.reviewNotes = reason;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update vehicle information.
     */
    public void updateVehicleInfo(VehicleInfo newVehicleInfo, DeliveryCapacity newCapacity) {
        this.vehicleInfo = Objects.requireNonNull(newVehicleInfo, "Vehicle info cannot be null");
        this.capacity = Objects.requireNonNull(newCapacity, "Capacity cannot be null");
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update availability schedule.
     */
    public void updateAvailabilitySchedule(AvailabilitySchedule newSchedule) {
        this.availabilitySchedule = Objects.requireNonNull(newSchedule, "Availability schedule cannot be null");
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update delivery person status.
     */
    public void updateStatus(DeliveryPersonStatus newStatus) {
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        // Can only change status if approved
        if (!approvalStatus.canWork() && newStatus != DeliveryPersonStatus.INACTIVE) {
            throw new IllegalStateException("Cannot change status - courier not approved");
        }
        
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
     * Update current location.
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
     * Check if courier can accept deliveries.
     */
    public boolean canAcceptDelivery(int orderWeight, int orderVolume) {
        if (!approvalStatus.canWork() || !status.isAvailable()) {
            return false;
        }
        
        return capacity.canAccommodate(
            currentOrderCount + 1,
            currentWeight + orderWeight,
            currentVolume + orderVolume
        );
    }
    
    /**
     * Assign a delivery to this courier.
     */
    public void assignDelivery(int orderWeight, int orderVolume) {
        if (!canAcceptDelivery(orderWeight, orderVolume)) {
            throw new IllegalStateException("Cannot accept delivery - not approved, not available, or capacity exceeded");
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
     * Get full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Check if courier is approved and can work.
     */
    public boolean canWork() {
        return approvalStatus.canWork();
    }
    
    /**
     * Check if courier is available for assignment.
     */
    public boolean isAvailable() {
        return approvalStatus.canWork() && status.isAvailable();
    }
    
    private String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return name.trim();
    }
    
    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.trim().toLowerCase();
    }
    
    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        return phoneNumber.trim();
    }
    
    private String validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        return city.trim();
    }
    
    // Getters
    public DeliveryPersonId getDeliveryPersonId() { return id; }
    public UserId getUserId() { return userId; }
    public TenantId getTenantId() { return tenantId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public VehicleInfo getVehicleInfo() { return vehicleInfo; }
    public DeliveryCapacity getCapacity() { return capacity; }
    public DeliveryPersonStatus getStatus() { return status; }
    public CourierApprovalStatus getApprovalStatus() { return approvalStatus; }
    public Location getCurrentLocation() { return currentLocation; }
    public AvailabilitySchedule getAvailabilitySchedule() { return availabilitySchedule; }
    public String getCity() { return city; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getNotes() { return notes; }
    public String getReviewNotes() { return reviewNotes; }
    public String getReviewerComments() { return reviewerComments; }
    public int getCurrentOrderCount() { return currentOrderCount; }
    public int getCurrentWeight() { return currentWeight; }
    public int getCurrentVolume() { return currentVolume; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getApprovedAt() { return approvedAt; }
}