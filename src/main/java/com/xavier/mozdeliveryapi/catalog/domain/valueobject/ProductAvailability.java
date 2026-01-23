package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

/**
 * Enumeration of product availability statuses.
 */
public enum ProductAvailability {
    /**
     * Product is available for ordering.
     */
    AVAILABLE,
    
    /**
     * Product is temporarily out of stock.
     */
    OUT_OF_STOCK,
    
    /**
     * Product is discontinued and no longer available.
     */
    DISCONTINUED,
    
    /**
     * Product is temporarily unavailable (e.g., seasonal).
     */
    UNAVAILABLE;
    
    /**
     * Check if product can be ordered.
     */
    public boolean canBeOrdered() {
        return this == AVAILABLE;
    }
    
    /**
     * Check if product is temporarily unavailable.
     */
    public boolean isTemporarilyUnavailable() {
        return this == OUT_OF_STOCK || this == UNAVAILABLE;
    }
    
    /**
     * Check if product is permanently unavailable.
     */
    public boolean isPermanentlyUnavailable() {
        return this == DISCONTINUED;
    }
}