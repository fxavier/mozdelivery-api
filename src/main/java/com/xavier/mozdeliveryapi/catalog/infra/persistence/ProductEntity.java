package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for product persistence.
 */
@Entity
@Table(name = "products")
public class ProductEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls;
    
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false, length = 20)
    private ProductAvailability availability;
    
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stock_info", columnDefinition = "jsonb")
    private StockInfoData stockInfo;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modifiers", columnDefinition = "jsonb")
    private List<ProductModifierData> modifiers;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected ProductEntity() {}
    
    // Constructor
    public ProductEntity(UUID id, UUID categoryId, String name, String description, 
                        List<String> imageUrls, BigDecimal basePrice, String currency,
                        ProductAvailability availability, Boolean isVisible, 
                        StockInfoData stockInfo, List<ProductModifierData> modifiers,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageUrls = imageUrls;
        this.basePrice = basePrice;
        this.currency = currency;
        this.availability = availability;
        this.isVisible = isVisible;
        this.stockInfo = stockInfo;
        this.modifiers = modifiers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public ProductAvailability getAvailability() { return availability; }
    public void setAvailability(ProductAvailability availability) { this.availability = availability; }
    
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    
    public StockInfoData getStockInfo() { return stockInfo; }
    public void setStockInfo(StockInfoData stockInfo) { this.stockInfo = stockInfo; }
    
    public List<ProductModifierData> getModifiers() { return modifiers; }
    public void setModifiers(List<ProductModifierData> modifiers) { this.modifiers = modifiers; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Data class for stock information JSON mapping.
     */
    public static class StockInfoData {
        private Boolean trackStock;
        private Integer currentStock;
        private Integer lowStockThreshold;
        private Integer maxStock;
        
        // Default constructor for JSON deserialization
        public StockInfoData() {}
        
        public StockInfoData(Boolean trackStock, Integer currentStock, Integer lowStockThreshold, Integer maxStock) {
            this.trackStock = trackStock;
            this.currentStock = currentStock;
            this.lowStockThreshold = lowStockThreshold;
            this.maxStock = maxStock;
        }
        
        // Getters and setters
        public Boolean getTrackStock() { return trackStock; }
        public void setTrackStock(Boolean trackStock) { this.trackStock = trackStock; }
        
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        
        public Integer getLowStockThreshold() { return lowStockThreshold; }
        public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
        
        public Integer getMaxStock() { return maxStock; }
        public void setMaxStock(Integer maxStock) { this.maxStock = maxStock; }
    }
    
    /**
     * Data class for product modifier JSON mapping.
     */
    public static class ProductModifierData {
        private String name;
        private String description;
        private String type;
        private Boolean required;
        private List<ProductModifierOptionData> options;
        
        // Default constructor for JSON deserialization
        public ProductModifierData() {}
        
        public ProductModifierData(String name, String description, String type, Boolean required, List<ProductModifierOptionData> options) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.required = required;
            this.options = options;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }
        
        public List<ProductModifierOptionData> getOptions() { return options; }
        public void setOptions(List<ProductModifierOptionData> options) { this.options = options; }
    }
    
    /**
     * Data class for product modifier option JSON mapping.
     */
    public static class ProductModifierOptionData {
        private String name;
        private String description;
        private BigDecimal priceAdjustment;
        private String currency;
        private Boolean available;
        
        // Default constructor for JSON deserialization
        public ProductModifierOptionData() {}
        
        public ProductModifierOptionData(String name, String description, BigDecimal priceAdjustment, String currency, Boolean available) {
            this.name = name;
            this.description = description;
            this.priceAdjustment = priceAdjustment;
            this.currency = currency;
            this.available = available;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getPriceAdjustment() { return priceAdjustment; }
        public void setPriceAdjustment(BigDecimal priceAdjustment) { this.priceAdjustment = priceAdjustment; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public Boolean getAvailable() { return available; }
        public void setAvailable(Boolean available) { this.available = available; }
    }
}