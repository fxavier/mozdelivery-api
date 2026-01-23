package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing stock information for a product.
 */
public record StockInfo(
    boolean trackStock,
    Integer currentStock,
    Integer lowStockThreshold,
    Integer maxStock
) implements ValueObject {
    
    public StockInfo {
        if (trackStock) {
            Objects.requireNonNull(currentStock, "Current stock cannot be null when tracking stock");
            if (currentStock < 0) {
                throw new IllegalArgumentException("Current stock cannot be negative");
            }
            
            if (lowStockThreshold != null && lowStockThreshold < 0) {
                throw new IllegalArgumentException("Low stock threshold cannot be negative");
            }
            
            if (maxStock != null && maxStock < 0) {
                throw new IllegalArgumentException("Max stock cannot be negative");
            }
            
            if (maxStock != null && currentStock > maxStock) {
                throw new IllegalArgumentException("Current stock cannot exceed max stock");
            }
        } else {
            // When not tracking stock, these values should be null
            if (currentStock != null || lowStockThreshold != null || maxStock != null) {
                throw new IllegalArgumentException("Stock values must be null when not tracking stock");
            }
        }
    }
    
    /**
     * Create stock info for products that don't track stock.
     */
    public static StockInfo noTracking() {
        return new StockInfo(false, null, null, null);
    }
    
    /**
     * Create stock info with unlimited stock tracking.
     */
    public static StockInfo unlimited(int currentStock) {
        return new StockInfo(true, currentStock, null, null);
    }
    
    /**
     * Create stock info with full tracking.
     */
    public static StockInfo tracked(int currentStock, Integer lowStockThreshold, Integer maxStock) {
        return new StockInfo(true, currentStock, lowStockThreshold, maxStock);
    }
    
    /**
     * Check if stock is low based on threshold.
     */
    public boolean isLowStock() {
        if (!trackStock || lowStockThreshold == null) {
            return false;
        }
        return currentStock <= lowStockThreshold;
    }
    
    /**
     * Check if stock is available for ordering.
     */
    public boolean hasStock() {
        if (!trackStock) {
            return true; // Assume available if not tracking
        }
        return currentStock > 0;
    }
    
    /**
     * Check if we can add more stock.
     */
    public boolean canAddStock(int quantity) {
        if (!trackStock) {
            return false; // Cannot add stock if not tracking
        }
        if (maxStock == null) {
            return true; // No limit
        }
        return currentStock + quantity <= maxStock;
    }
    
    /**
     * Reduce stock by quantity.
     */
    public StockInfo reduceStock(int quantity) {
        if (!trackStock) {
            throw new IllegalStateException("Cannot reduce stock when not tracking");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (currentStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        
        return new StockInfo(trackStock, currentStock - quantity, lowStockThreshold, maxStock);
    }
    
    /**
     * Add stock by quantity.
     */
    public StockInfo addStock(int quantity) {
        if (!trackStock) {
            throw new IllegalStateException("Cannot add stock when not tracking");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (!canAddStock(quantity)) {
            throw new IllegalArgumentException("Would exceed maximum stock");
        }
        
        return new StockInfo(trackStock, currentStock + quantity, lowStockThreshold, maxStock);
    }
}