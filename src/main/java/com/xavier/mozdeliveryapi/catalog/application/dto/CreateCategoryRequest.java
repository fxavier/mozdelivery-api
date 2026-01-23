package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.util.Objects;

/**
 * Request DTO for creating a new category.
 */
public record CreateCategoryRequest(
    String merchantId,
    String catalogId,
    String name,
    String description,
    String imageUrl,
    Integer displayOrder
) {
    
    public CreateCategoryRequest {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (displayOrder != null && displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
    }
}