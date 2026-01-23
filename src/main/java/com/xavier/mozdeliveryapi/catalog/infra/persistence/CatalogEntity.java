package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.time.Instant;
import java.util.UUID;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for Catalog aggregate.
 */
@Entity
@Table(name = "catalogs")
public class CatalogEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CatalogStatus status;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected CatalogEntity() {}
    
    // Constructor for creating from domain object
    public CatalogEntity(Catalog catalog) {
        this.id = catalog.getCatalogId().value();
        this.merchantId = catalog.getMerchantId().value();
        this.name = catalog.getName();
        this.description = catalog.getDescription();
        this.status = catalog.getStatus();
        this.displayOrder = catalog.getDisplayOrder();
        this.createdAt = catalog.getCreatedAt();
        this.updatedAt = catalog.getUpdatedAt();
    }
    
    /**
     * Convert to domain object.
     */
    public Catalog toDomain() {
        int displayOrderValue = displayOrder != null ? displayOrder.intValue() : 0;
        return new Catalog(
            CatalogId.of(id),
            MerchantId.of(merchantId),
            name,
            description,
            new java.util.ArrayList<>(), // categoryIds - empty list for now
            status,
            displayOrderValue,
            createdAt,
            updatedAt
        );
    }
    
    /**
     * Update from domain object.
     */
    public void updateFrom(Catalog catalog) {
        this.merchantId = catalog.getMerchantId().value();
        this.name = catalog.getName();
        this.description = catalog.getDescription();
        this.status = catalog.getStatus();
        this.displayOrder = catalog.getDisplayOrder();
        this.updatedAt = catalog.getUpdatedAt();
    }
    
    // Getters
    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public CatalogStatus getStatus() { return status; }
    public Integer getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}