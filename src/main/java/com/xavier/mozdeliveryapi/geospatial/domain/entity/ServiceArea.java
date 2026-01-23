package com.xavier.mozdeliveryapi.geospatial.domain.entity;

import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Boundary;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.City;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.ServiceAreaId;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * ServiceArea aggregate representing a geographic area where a tenant provides delivery services.
 * Each service area is defined by a polygon boundary and belongs to a specific tenant and city.
 */
public class ServiceArea extends AggregateRoot<ServiceAreaId> {
    
    @NotNull
    private final ServiceAreaId id;
    
    @NotNull
    private final TenantId tenantId;
    
    @NotNull
    private final City city;
    
    @NotNull
    private final Boundary boundary;
    
    private boolean active;
    
    private final Instant createdAt;
    private Instant updatedAt;
    
    private ServiceArea(ServiceAreaId id, TenantId tenantId, City city, Boundary boundary) {
        this.id = Objects.requireNonNull(id, "Service Area ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.city = Objects.requireNonNull(city, "City cannot be null");
        this.boundary = Objects.requireNonNull(boundary, "Boundary cannot be null");
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    
    @Override
    public ServiceAreaId getId() {
        return id;
    }
    
    public static ServiceArea create(TenantId tenantId, City city, Boundary boundary) {
        ServiceAreaId id = ServiceAreaId.generate();
        return new ServiceArea(id, tenantId, city, boundary);
    }
    
    public static ServiceArea reconstitute(ServiceAreaId id, TenantId tenantId, City city, 
                                         Boundary boundary, boolean active, 
                                         Instant createdAt, Instant updatedAt) {
        ServiceArea serviceArea = new ServiceArea(id, tenantId, city, boundary);
        serviceArea.active = active;
        serviceArea.updatedAt = updatedAt;
        return serviceArea;
    }
    
    /**
     * Check if a location is within this service area.
     */
    public boolean contains(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        return boundary.contains(location);
    }
    
    /**
     * Activate this service area.
     */
    public void activate() {
        if (!active) {
            this.active = true;
            this.updatedAt = Instant.now();
            // Could publish ServiceAreaActivatedEvent here
        }
    }
    
    /**
     * Deactivate this service area.
     */
    public void deactivate() {
        if (active) {
            this.active = false;
            this.updatedAt = Instant.now();
            // Could publish ServiceAreaDeactivatedEvent here
        }
    }
    
    /**
     * Check if this service area overlaps with another service area.
     */
    public boolean overlapsWith(ServiceArea other) {
        Objects.requireNonNull(other, "Other service area cannot be null");
        return this.boundary.intersects(other.boundary);
    }
    
    /**
     * Calculate the area covered by this service area in square meters.
     */
    public double getAreaInSquareMeters() {
        return boundary.getAreaInSquareMeters();
    }
    
    // Getters
    public TenantId getTenantId() {
        return tenantId;
    }
    
    public City getCity() {
        return city;
    }
    
    public Boundary getBoundary() {
        return boundary;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceArea that = (ServiceArea) o;
        return Objects.equals(getId(), that.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return String.format("ServiceArea{id=%s, tenantId=%s, city=%s, active=%s}", 
                           getId(), tenantId, city.getName(), active);
    }
}