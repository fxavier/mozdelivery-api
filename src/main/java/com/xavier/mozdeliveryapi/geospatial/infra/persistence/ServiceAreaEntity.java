package com.xavier.mozdeliveryapi.geospatial.infra.persistence;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity for ServiceArea aggregate.
 * Uses PostGIS geometry types for spatial operations.
 */
@Entity
@Table(name = "service_areas")
public class ServiceAreaEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;
    
    @Column(name = "city_center_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal cityCenterLatitude;
    
    @Column(name = "city_center_lon", nullable = false, precision = 11, scale = 8)
    private BigDecimal cityCenterLongitude;
    
    @Column(name = "boundary", nullable = false, columnDefinition = "geometry(POLYGON, 4326)")
    private Geometry boundary;
    
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    protected ServiceAreaEntity() {
        // JPA constructor
    }
    
    public ServiceAreaEntity(UUID id, UUID tenantId, String city, String countryCode,
                           BigDecimal cityCenterLatitude, BigDecimal cityCenterLongitude,
                           Geometry boundary, Boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.city = city;
        this.countryCode = countryCode;
        this.cityCenterLatitude = cityCenterLatitude;
        this.cityCenterLongitude = cityCenterLongitude;
        this.boundary = boundary;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // Getters and setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public BigDecimal getCityCenterLatitude() {
        return cityCenterLatitude;
    }
    
    public void setCityCenterLatitude(BigDecimal cityCenterLatitude) {
        this.cityCenterLatitude = cityCenterLatitude;
    }
    
    public BigDecimal getCityCenterLongitude() {
        return cityCenterLongitude;
    }
    
    public void setCityCenterLongitude(BigDecimal cityCenterLongitude) {
        this.cityCenterLongitude = cityCenterLongitude;
    }
    
    public Geometry getBoundary() {
        return boundary;
    }
    
    public void setBoundary(Geometry boundary) {
        this.boundary = boundary;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceAreaEntity that = (ServiceAreaEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("ServiceAreaEntity{id=%s, tenantId=%s, city='%s', active=%s}", 
                           id, tenantId, city, active);
    }
}
