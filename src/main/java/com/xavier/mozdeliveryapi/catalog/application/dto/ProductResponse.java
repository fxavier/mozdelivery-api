package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;

/**
 * Response DTO for product information.
 */
public record ProductResponse(
    String id,
    String merchantId,
    String categoryId,
    String name,
    String description,
    List<String> imageUrls,
    BigDecimal price,
    String currency,
    ProductAvailability availability,
    boolean visible,
    boolean canBeOrdered,
    StockInfoResponse stockInfo,
    Instant createdAt,
    Instant updatedAt
) {
    
    /**
     * Nested DTO for stock information.
     */
    public record StockInfoResponse(
        boolean trackStock,
        Integer currentStock,
        Integer lowStockThreshold,
        Integer maxStock,
        boolean isLowStock,
        boolean hasStock
    ) {
    }
}