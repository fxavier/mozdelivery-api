package com.xavier.mozdeliveryapi.catalog.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.catalog.domain.event.CatalogActivatedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.event.CatalogArchivedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.event.CatalogCreatedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.event.CatalogDeactivatedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;

/**
 * Catalog aggregate root representing a collection of categories and products for a merchant.
 */
public class Catalog extends AggregateRoot<CatalogId> {
    
    private final CatalogId id;
    private final MerchantId merchantId;
    private String name;
    private String description;
    private List<CategoryId> categoryIds;
    private CatalogStatus status;
    private int displayOrder;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new catalog
    public Catalog(CatalogId id, MerchantId merchantId, String name, String description) {
        this.id = Objects.requireNonNull(id, "Catalog ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.categoryIds = new ArrayList<>();
        this.status = CatalogStatus.DRAFT;
        this.displayOrder = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new CatalogCreatedEvent(id, merchantId, name, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public Catalog(CatalogId id, MerchantId merchantId, String name, String description, 
                  List<CategoryId> categoryIds, CatalogStatus status, int displayOrder,
                  Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Catalog ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.categoryIds = Objects.requireNonNull(categoryIds, "Category IDs cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.displayOrder = validateDisplayOrder(displayOrder);
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected CatalogId getId() {
        return id;
    }
    
    /**
     * Update catalog details.
     */
    public void updateDetails(String name, String description) {
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit catalog in status: " + status);
        }
        
        this.name = validateName(name);
        this.description = description;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update display order.
     */
    public void updateDisplayOrder(int displayOrder) {
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit catalog in status: " + status);
        }
        
        this.displayOrder = validateDisplayOrder(displayOrder);
        this.updatedAt = Instant.now();
    }
    
    /**
     * Add a category to this catalog.
     */
    public void addCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit catalog in status: " + status);
        }
        
        if (categoryIds.contains(categoryId)) {
            return; // Already added
        }
        
        categoryIds.add(categoryId);
        this.updatedAt = Instant.now();
    }
    
    /**
     * Remove a category from this catalog.
     */
    public void removeCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit catalog in status: " + status);
        }
        
        categoryIds.remove(categoryId);
        this.updatedAt = Instant.now();
    }
    
    /**
     * Activate the catalog (make it visible to customers).
     */
    public void activate() {
        if (!status.canBeActivated()) {
            throw new IllegalStateException("Cannot activate catalog in status: " + status);
        }
        
        if (categoryIds.isEmpty()) {
            throw new IllegalStateException("Cannot activate catalog without categories");
        }
        
        CatalogStatus oldStatus = this.status;
        this.status = CatalogStatus.ACTIVE;
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new CatalogActivatedEvent(id, merchantId, oldStatus, updatedAt));
    }
    
    /**
     * Deactivate the catalog (hide from customers but keep editable).
     */
    public void deactivate() {
        if (!status.canBeDeactivated()) {
            throw new IllegalStateException("Cannot deactivate catalog in status: " + status);
        }
        
        CatalogStatus oldStatus = this.status;
        this.status = CatalogStatus.INACTIVE;
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new CatalogDeactivatedEvent(id, merchantId, oldStatus, updatedAt));
    }
    
    /**
     * Archive the catalog (permanently hide and make non-editable).
     */
    public void archive() {
        if (status == CatalogStatus.ARCHIVED) {
            return; // Already archived
        }
        
        CatalogStatus oldStatus = this.status;
        this.status = CatalogStatus.ARCHIVED;
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new CatalogArchivedEvent(id, merchantId, oldStatus, updatedAt));
    }
    
    /**
     * Check if catalog is visible to customers.
     */
    public boolean isVisible() {
        return status.isVisible();
    }
    
    /**
     * Check if catalog can be edited.
     */
    public boolean canBeEdited() {
        return status.canBeEdited();
    }
    
    /**
     * Check if catalog has categories.
     */
    public boolean hasCategories() {
        return !categoryIds.isEmpty();
    }
    
    /**
     * Get number of categories.
     */
    public int getCategoryCount() {
        return categoryIds.size();
    }
    
    private String validateName(String name) {
        Objects.requireNonNull(name, "Catalog name cannot be null");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Catalog name cannot be empty");
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Catalog name cannot exceed 100 characters");
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
    public CatalogId getCatalogId() { return id; }
    public MerchantId getMerchantId() { return merchantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<CategoryId> getCategoryIds() { return new ArrayList<>(categoryIds); }
    public CatalogStatus getStatus() { return status; }
    public int getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}