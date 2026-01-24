package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.time.Instant;
import java.util.UUID;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for Category aggregate.
 */
@Entity
@Table(name = "categories")
public class CategoryEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "catalog_id", nullable = false)
    private UUID catalogId;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected CategoryEntity() {}
    
    // Constructor for creating from domain object
    public CategoryEntity(Category category) {
        this.id = category.getCategoryId().value();
        this.catalogId = category.getCatalogId().value();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.isVisible = category.isVisible();
        this.displayOrder = category.getDisplayOrder();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }
    
    /**
     * Convert to domain object.
     */
    public Category toDomain(MerchantId merchantId) {
        return new Category(
            CategoryId.of(id),
            merchantId,
            CatalogId.of(catalogId),
            name,
            description,
            imageUrl,
            displayOrder != null ? displayOrder.intValue() : 0,
            isVisible != null ? isVisible.booleanValue() : true,
            createdAt,
            updatedAt
        );
    }
    
    /**
     * Update from domain object.
     */
    public void updateFrom(Category category) {
        this.catalogId = category.getCatalogId().value();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.isVisible = category.isVisible();
        this.displayOrder = category.getDisplayOrder();
        this.updatedAt = category.getUpdatedAt();
    }
    
    // Getters
    public UUID getId() { return id; }
    public UUID getCatalogId() { return catalogId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Boolean getIsVisible() { return isVisible; }
    public Integer getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}