package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.util.Objects;

/**
 * Request DTO for creating a new catalog.
 */
public record CreateCatalogRequest(
    String merchantId,
    String name,
    String description,
    Integer displayOrder
) {
    
    public CreateCatalogRequest {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (displayOrder != null && displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
    }
}