package com.xavier.mozdeliveryapi.merchant.domain.entity;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.merchant.domain.event.MerchantApprovedEvent;
import com.xavier.mozdeliveryapi.merchant.domain.event.MerchantConfigurationUpdatedEvent;
import com.xavier.mozdeliveryapi.merchant.domain.event.MerchantCreatedEvent;
import com.xavier.mozdeliveryapi.merchant.domain.event.MerchantRejectedEvent;
import com.xavier.mozdeliveryapi.merchant.domain.event.MerchantStatusChangedEvent;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.ApprovalStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.BusinessDetails;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantConfiguration;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Merchant aggregate root representing a business entity using the platform.
 */
public class Merchant extends AggregateRoot<MerchantId> {
    
    private final MerchantId id;
    private BusinessDetails businessDetails;
    private Vertical vertical;
    private MerchantStatus status;
    private MerchantConfiguration configuration;
    private ComplianceSettings complianceSettings;
    private ApprovalStatus approvalStatus;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new merchant (registration)
    public Merchant(MerchantId id, BusinessDetails businessDetails, Vertical vertical) {
        this.id = Objects.requireNonNull(id, "Merchant ID cannot be null");
        this.businessDetails = Objects.requireNonNull(businessDetails, "Business details cannot be null");
        this.vertical = Objects.requireNonNull(vertical, "Vertical cannot be null");
        this.status = MerchantStatus.PENDING;
        this.configuration = MerchantConfiguration.defaultFor(vertical);
        this.complianceSettings = ComplianceSettings.defaultFor(vertical);
        this.approvalStatus = ApprovalStatus.pending();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new MerchantCreatedEvent(id, businessDetails.businessName(), vertical, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public Merchant(MerchantId id, BusinessDetails businessDetails, Vertical vertical, 
                   MerchantStatus status, MerchantConfiguration configuration, 
                   ComplianceSettings complianceSettings, ApprovalStatus approvalStatus,
                   Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Merchant ID cannot be null");
        this.businessDetails = Objects.requireNonNull(businessDetails, "Business details cannot be null");
        this.vertical = Objects.requireNonNull(vertical, "Vertical cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.configuration = Objects.requireNonNull(configuration, "Configuration cannot be null");
        this.complianceSettings = Objects.requireNonNull(complianceSettings, "Compliance settings cannot be null");
        this.approvalStatus = Objects.requireNonNull(approvalStatus, "Approval status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected MerchantId getId() {
        return id;
    }
    
    /**
     * Approve the merchant for operations.
     */
    public void approve(String approvedBy) {
        Objects.requireNonNull(approvedBy, "Approved by cannot be null");
        
        if (!status.equals(MerchantStatus.PENDING)) {
            throw new IllegalStateException("Only pending merchants can be approved");
        }
        
        MerchantStatus oldStatus = this.status;
        this.status = MerchantStatus.ACTIVE;
        this.approvalStatus = ApprovalStatus.approved(approvedBy);
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantStatusChangedEvent(id, oldStatus, status, updatedAt));
        registerEvent(new MerchantApprovedEvent(id, approvedBy, updatedAt));
    }
    
    /**
     * Reject the merchant application.
     */
    public void reject(String rejectionReason, String rejectedBy) {
        Objects.requireNonNull(rejectionReason, "Rejection reason cannot be null");
        Objects.requireNonNull(rejectedBy, "Rejected by cannot be null");
        
        if (!status.equals(MerchantStatus.PENDING)) {
            throw new IllegalStateException("Only pending merchants can be rejected");
        }
        
        MerchantStatus oldStatus = this.status;
        this.status = MerchantStatus.REJECTED;
        this.approvalStatus = ApprovalStatus.rejected(rejectionReason, rejectedBy);
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantStatusChangedEvent(id, oldStatus, status, updatedAt));
        registerEvent(new MerchantRejectedEvent(id, rejectionReason, rejectedBy, updatedAt));
    }
    
    /**
     * Update merchant configuration.
     */
    public void updateConfiguration(MerchantConfiguration newConfiguration) {
        Objects.requireNonNull(newConfiguration, "Configuration cannot be null");
        
        if (!canAccessSystem()) {
            throw new IllegalStateException("Merchant cannot update configuration in current status");
        }
        
        if (!newConfiguration.acceptsAnyPayment()) {
            throw new IllegalArgumentException("Merchant must accept at least one payment method");
        }
        
        MerchantConfiguration oldConfiguration = this.configuration;
        this.configuration = newConfiguration;
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantConfigurationUpdatedEvent(id, oldConfiguration, newConfiguration, updatedAt));
    }
    
    /**
     * Update business details.
     */
    public void updateBusinessDetails(BusinessDetails newBusinessDetails) {
        Objects.requireNonNull(newBusinessDetails, "Business details cannot be null");
        
        if (!canAccessSystem()) {
            throw new IllegalStateException("Merchant cannot update business details in current status");
        }
        
        this.businessDetails = newBusinessDetails;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Activate the merchant.
     */
    public void activate() {
        if (!approvalStatus.isApproved()) {
            throw new IllegalStateException("Merchant must be approved before activation");
        }
        
        if (status == MerchantStatus.ACTIVE) {
            return; // Already active
        }
        
        MerchantStatus oldStatus = this.status;
        this.status = MerchantStatus.ACTIVE;
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantStatusChangedEvent(id, oldStatus, status, updatedAt));
    }
    
    /**
     * Deactivate the merchant.
     */
    public void deactivate() {
        if (status == MerchantStatus.INACTIVE) {
            return; // Already inactive
        }
        
        MerchantStatus oldStatus = this.status;
        this.status = MerchantStatus.INACTIVE;
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantStatusChangedEvent(id, oldStatus, status, updatedAt));
    }
    
    /**
     * Suspend the merchant.
     */
    public void suspend(String reason) {
        Objects.requireNonNull(reason, "Suspension reason cannot be null");
        
        if (status == MerchantStatus.SUSPENDED) {
            return; // Already suspended
        }
        
        MerchantStatus oldStatus = this.status;
        this.status = MerchantStatus.SUSPENDED;
        this.updatedAt = Instant.now();
        
        registerEvent(new MerchantStatusChangedEvent(id, oldStatus, status, updatedAt));
    }
    
    /**
     * Check if merchant can process orders.
     */
    public boolean canProcessOrders() {
        return status.canProcessOrders();
    }
    
    /**
     * Check if merchant can access the system.
     */
    public boolean canAccessSystem() {
        return status.canAccessSystem();
    }
    
    /**
     * Check if merchant requires prescription validation.
     */
    public boolean requiresPrescriptionValidation() {
        return vertical.requiresPrescriptionValidation() || 
               complianceSettings.requiresPrescriptionValidation();
    }
    
    /**
     * Check if merchant requires age verification.
     */
    public boolean requiresAgeVerification() {
        return vertical.requiresAgeVerification() || 
               complianceSettings.requiresAgeVerification();
    }
    
    /**
     * Check if merchant is publicly visible (approved and active/inactive).
     */
    public boolean isPubliclyVisible() {
        return approvalStatus.isApproved() && (status == MerchantStatus.ACTIVE || status == MerchantStatus.INACTIVE);
    }
    
    // Getters
    public MerchantId getMerchantId() { return id; }
    public BusinessDetails getBusinessDetails() { return businessDetails; }
    public String getBusinessName() { return businessDetails.businessName(); }
    public String getDisplayName() { return businessDetails.displayName(); }
    public Vertical getVertical() { return vertical; }
    public MerchantStatus getStatus() { return status; }
    public MerchantConfiguration getConfiguration() { return configuration; }
    public ComplianceSettings getComplianceSettings() { return complianceSettings; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}