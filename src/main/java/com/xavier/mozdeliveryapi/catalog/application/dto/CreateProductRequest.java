package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Request DTO for creating a new product.
 */
public record CreateProductRequest(
    String merchantId,
    String categoryId,
    String name,
    String description,
    List<String> imageUrls,
    BigDecimal price,
    String currency,
    Boolean trackStock,
    Integer currentStock,
    Integer lowStockThreshold,
    Integer maxStock
) {
    
    public CreateProductRequest {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        if (trackStock != null && trackStock) {
            Objects.requireNonNull(currentStock, "Current stock cannot be null when tracking stock");
            if (currentStock < 0) {
                throw new IllegalArgumentException("Current stock cannot be negative");
            }
        }
    }
}