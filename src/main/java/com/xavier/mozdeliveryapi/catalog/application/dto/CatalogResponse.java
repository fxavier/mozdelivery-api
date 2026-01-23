package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.time.Instant;
import java.util.List;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;

/**
 * Response DTO for catalog information.
 */
public record CatalogResponse(
    String id,
    String merchantId,
    String name,
    String description,
    List<String> categoryIds,
    CatalogStatus status,
    int displayOrder,
    boolean visible,
    int categoryCount,
    Instant createdAt,
    Instant updatedAt
) {
}