package com.xavier.mozdeliveryapi.catalog.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.catalog.domain.event.ProductAvailabilityChangedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.event.ProductCreatedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.event.ProductStockUpdatedEvent;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductModifier;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Product aggregate root representing a product within a category.
 */
public class Product extends AggregateRoot<ProductId> {
    
    private final ProductId id;
    private final MerchantId merchantId;
    private final CategoryId categoryId;
    private String name;
    private String description;
    private List<String> imageUrls;
    private Money price;
    private Currency currency;
    private ProductAvailability availability;
    private StockInfo stockInfo;
    private List<ProductModifier> modifiers;
    private boolean visible;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new product
    public Product(ProductId id, MerchantId merchantId, CategoryId categoryId, 
                  String name, String description, Money price) {
        this.id = Objects.requireNonNull(id, "Product ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.currency = price.currency();
        this.availability = ProductAvailability.AVAILABLE;
        this.stockInfo = StockInfo.noTracking();
        this.imageUrls = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.visible = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new ProductCreatedEvent(id, merchantId, categoryId, name, createdAt));
    }
    
    // Constructor for reconstituting from persistence
    public Product(ProductId id, MerchantId merchantId, CategoryId categoryId, 
                  String name, String description, List<String> imageUrls, Money price, 
                  ProductAvailability availability, StockInfo stockInfo, 
                  List<ProductModifier> modifiers, boolean visible, 
                  Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Product ID cannot be null");
        this.merchantId = Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID cannot be null");
        this.name = validateName(name);
        this.description = description;
        this.imageUrls = Objects.requireNonNull(imageUrls, "Image URLs cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.currency = price.currency();
        this.availability = Objects.requireNonNull(availability, "Availability cannot be null");
        this.stockInfo = Objects.requireNonNull(stockInfo, "Stock info cannot be null");
        this.modifiers = Objects.requireNonNull(modifiers, "Modifiers cannot be null");
        this.visible = visible;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected ProductId getId() {
        return id;
    }
    
    /**
     * Update product details.
     */
    public void updateDetails(String name, String description, Money price) {
        this.name = validateName(name);
        this.description = description;
        
        if (!price.currency().equals(this.currency)) {
            throw new IllegalArgumentException("Cannot change product currency");
        }
        this.price = price;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update product images.
     */
    public void updateImages(List<String> imageUrls) {
        this.imageUrls = new ArrayList<>(Objects.requireNonNull(imageUrls, "Image URLs cannot be null"));
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update product availability.
     */
    public void updateAvailability(ProductAvailability newAvailability) {
        Objects.requireNonNull(newAvailability, "Availability cannot be null");
        
        if (this.availability == newAvailability) {
            return; // No change
        }
        
        ProductAvailability oldAvailability = this.availability;
        this.availability = newAvailability;
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(new ProductAvailabilityChangedEvent(id, merchantId, oldAvailability, newAvailability, updatedAt));
    }
    
    /**
     * Update stock information.
     */
    public void updateStockInfo(StockInfo newStockInfo) {
        Objects.requireNonNull(newStockInfo, "Stock info cannot be null");
        
        StockInfo oldStockInfo = this.stockInfo;
        this.stockInfo = newStockInfo;
        this.updatedAt = Instant.now();
        
        // Register domain event if stock tracking changed or stock levels changed
        if (oldStockInfo.trackStock() != newStockInfo.trackStock() ||
            (newStockInfo.trackStock() && !Objects.equals(oldStockInfo.currentStock(), newStockInfo.currentStock()))) {
            registerEvent(new ProductStockUpdatedEvent(id, merchantId, oldStockInfo, newStockInfo, updatedAt));
        }
    }
    
    /**
     * Reduce stock by quantity (for order processing).
     */
    public void reduceStock(int quantity) {
        if (!stockInfo.trackStock()) {
            return; // No stock tracking, nothing to reduce
        }
        
        StockInfo oldStockInfo = this.stockInfo;
        this.stockInfo = stockInfo.reduceStock(quantity);
        this.updatedAt = Instant.now();
        
        // Auto-update availability if out of stock
        if (!stockInfo.hasStock() && availability == ProductAvailability.AVAILABLE) {
            updateAvailability(ProductAvailability.OUT_OF_STOCK);
        }
        
        // Register domain event
        registerEvent(new ProductStockUpdatedEvent(id, merchantId, oldStockInfo, stockInfo, updatedAt));
    }
    
    /**
     * Add stock by quantity (for restocking).
     */
    public void addStock(int quantity) {
        if (!stockInfo.trackStock()) {
            throw new IllegalStateException("Cannot add stock when not tracking stock");
        }
        
        StockInfo oldStockInfo = this.stockInfo;
        this.stockInfo = stockInfo.addStock(quantity);
        this.updatedAt = Instant.now();
        
        // Auto-update availability if back in stock
        if (stockInfo.hasStock() && availability == ProductAvailability.OUT_OF_STOCK) {
            updateAvailability(ProductAvailability.AVAILABLE);
        }
        
        // Register domain event
        registerEvent(new ProductStockUpdatedEvent(id, merchantId, oldStockInfo, stockInfo, updatedAt));
    }
    
    /**
     * Update product modifiers.
     */
    public void updateModifiers(List<ProductModifier> modifiers) {
        this.modifiers = new ArrayList<>(Objects.requireNonNull(modifiers, "Modifiers cannot be null"));
        this.updatedAt = Instant.now();
    }
    
    /**
     * Show product to customers.
     */
    public void show() {
        if (visible) {
            return; // Already visible
        }
        this.visible = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Hide product from customers.
     */
    public void hide() {
        if (!visible) {
            return; // Already hidden
        }
        this.visible = false;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Check if product can be ordered.
     */
    public boolean canBeOrdered() {
        return visible && availability.canBeOrdered() && 
               (!stockInfo.trackStock() || stockInfo.hasStock());
    }
    
    /**
     * Check if product is low on stock.
     */
    public boolean isLowStock() {
        return stockInfo.isLowStock();
    }
    
    private String validateName(String name) {
        Objects.requireNonNull(name, "Product name cannot be null");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (trimmed.length() > 200) {
            throw new IllegalArgumentException("Product name cannot exceed 200 characters");
        }
        return trimmed;
    }
    
    // Getters
    public ProductId getProductId() { return id; }
    public MerchantId getMerchantId() { return merchantId; }
    public CategoryId getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public Money getPrice() { return price; }
    public Currency getCurrency() { return currency; }
    public ProductAvailability getAvailability() { return availability; }
    public StockInfo getStockInfo() { return stockInfo; }
    public List<ProductModifier> getModifiers() { return new ArrayList<>(modifiers); }
    public boolean isVisible() { return visible; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}