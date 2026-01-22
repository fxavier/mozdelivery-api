package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Tenant aggregate root representing a business entity using the platform.
 */
public class Tenant extends AggregateRoot<TenantId> {
    
    private final TenantId id;
    private String name;
    private Vertical vertical;
    private TenantStatus status;
    private TenantConfiguration configuration;
    private ComplianceSettings complianceSettings;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new tenant
    public Tenant(TenantId id, String name, Vertical vertical) {
        this.id = Objects.requireNonNull(id, "Tenant ID cannot be null");
        this.name = validateName(name);
        this.vertical = Objects.requireNonNull(vertical, "Vertical cannot be null");
        this.status = TenantStatus.ACTIVE;
        this.configuration = TenantConfiguration.defaultFor(vertical);
        this.complianceSettings = ComplianceSettings.defaultFor(vertical);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new TenantCreatedEvent(id, name, vertical, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public Tenant(TenantId id, String name, Vertical vertical, TenantStatus status,
                  TenantConfiguration configuration, ComplianceSettings complianceSettings,
                  Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Tenant ID cannot be null");
        this.name = validateName(name);
        this.vertical = Objects.requireNonNull(vertical, "Vertical cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.configuration = Objects.requireNonNull(configuration, "Configuration cannot be null");
        this.complianceSettings = Objects.requireNonNull(complianceSettings, "Compliance settings cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected TenantId getId() {
        return id;
    }
    
    /**
     * Update tenant configuration.
     */
    public void updateConfiguration(TenantConfiguration newConfiguration) {
        Objects.requireNonNull(newConfiguration, "Configuration cannot be null");
        
        if (!newConfiguration.acceptsAnyPayment()) {
            throw new IllegalArgumentException("Tenant must accept at least one payment method");
        }
        
        TenantConfiguration oldConfiguration = this.configuration;
        this.configuration = newConfiguration;
        this.updatedAt = Instant.now();
        
        registerEvent(new TenantConfigurationUpdatedEvent(id, oldConfiguration, newConfiguration, updatedAt));
    }
    
    /**
     * Update compliance settings.
     */
    public void updateComplianceSettings(ComplianceSettings newSettings) {
        Objects.requireNonNull(newSettings, "Compliance settings cannot be null");
        
        ComplianceSettings oldSettings = this.complianceSettings;
        this.complianceSettings = newSettings;
        this.updatedAt = Instant.now();
        
        registerEvent(new TenantComplianceUpdatedEvent(id, oldSettings, newSettings, updatedAt));
    }
    
    /**
     * Activate the tenant.
     */
    public void activate() {
        if (status == TenantStatus.ACTIVE) {
            return; // Already active
        }
        
        TenantStatus oldStatus = this.status;
        this.status = TenantStatus.ACTIVE;
        this.updatedAt = Instant.now();
        
        registerEvent(new TenantStatusChangedEvent(id, oldStatus, status, updatedAt));
    }
    
    /**
     * Deactivate the tenant.
     */
    public void deactivate() {
        if (status == TenantStatus.INACTIVE) {
            return; // Already inactive
        }
        
        TenantStatus oldStatus = this.status;
        this.status = TenantStatus.INACTIVE;
        this.updatedAt = Instant.now();
        
        registerEvent(new TenantStatusChangedEvent(id, oldStatus, status, updatedAt));
    }
    
    /**
     * Suspend the tenant.
     */
    public void suspend(String reason) {
        Objects.requireNonNull(reason, "Suspension reason cannot be null");
        
        if (status == TenantStatus.SUSPENDED) {
            return; // Already suspended
        }
        
        TenantStatus oldStatus = this.status;
        this.status = TenantStatus.SUSPENDED;
        this.updatedAt = Instant.now();
        
        registerEvent(new TenantSuspendedEvent(id, reason, updatedAt));
    }
    
    /**
     * Check if tenant can process orders.
     */
    public boolean canProcessOrders() {
        return status.canProcessOrders();
    }
    
    /**
     * Check if tenant can access the system.
     */
    public boolean canAccessSystem() {
        return status.canAccessSystem();
    }
    
    /**
     * Check if tenant requires prescription validation.
     */
    public boolean requiresPrescriptionValidation() {
        return vertical.requiresPrescriptionValidation() || 
               complianceSettings.requiresPrescriptionValidation();
    }
    
    /**
     * Check if tenant requires age verification.
     */
    public boolean requiresAgeVerification() {
        return vertical.requiresAgeVerification() || 
               complianceSettings.requiresAgeVerification();
    }
    
    private String validateName(String name) {
        Objects.requireNonNull(name, "Tenant name cannot be null");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Tenant name cannot be empty");
        }
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Tenant name cannot exceed 255 characters");
        }
        return trimmed;
    }
    
    // Getters
    public TenantId getTenantId() { return id; }
    public String getName() { return name; }
    public Vertical getVertical() { return vertical; }
    public TenantStatus getStatus() { return status; }
    public TenantConfiguration getConfiguration() { return configuration; }
    public ComplianceSettings getComplianceSettings() { return complianceSettings; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}