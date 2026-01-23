package com.xavier.mozdeliveryapi.catalog.domain.entity;

import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Category entity representing a product category within a catalog.
 */
public class Category extends AggregateRoot<CategoryId> {
    
    private final CategoryId id;
    private final MerchantId merchantId;
    private final CatalogId catalogId;
    private String name;
    private String description;
    private String imageUrl;
    private int displayOrder;
    private boolean visible;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new category
    public Category(CategoryId id, MerchantId merchantId, CatalogId catalogId, 
                   String name, String description, int displayOrder) {
        this.id = Objects.requireNonNull(id, "Category ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.catalogId = Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.displayOrder = validateDisplayOrder(displayOrder);
        this.visible = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Constructor for reconstituting from persistence
    public Category(CategoryId id, MerchantId merchantId, CatalogId catalogId, 
                   String name, String description, String imageUrl, int displayOrder, 
                   boolean visible, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Category ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.catalogId = Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.imageUrl = imageUrl;
        this.displayOrder = validateDisplayOrder(displayOrder);
        this.visible = visible;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected CategoryId getId() {
        return id;
    }
    
    /**
     * Update category details.
     */
    public void updateDetails(String name, String description, String imageUrl) {
        this.name = validateName(name);
        this.description = description;
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update display order.
     */
    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = validateDisplayOrder(displayOrder);
        this.updatedAt = Instant.now();
    }
    
    /**
     * Show category to customers.
     */
    public void show() {
        if (visible) {
            return; // Already visible
        }
        this.visible = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Hide category from customers.
     */
    public void hide() {
        if (!visible) {
            return; // Already hidden
        }
        this.visible = false;
        this.updatedAt = Instant.now();
    }
    
    private String validateName(String name) {
        Objects.requireNonNull(name, "Category name cannot be null");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
        return trimmed;
    }
    
    private int validateDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
        return displayOrder;
    }
    
    // Getters
    public CategoryId getCategoryId() { return id; }
    public MerchantId getMerchantId() { return merchantId; }
    public CatalogId getCatalogId() { return catalogId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getDisplayOrder() { return displayOrder; }
    public boolean isVisible() { return visible; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}