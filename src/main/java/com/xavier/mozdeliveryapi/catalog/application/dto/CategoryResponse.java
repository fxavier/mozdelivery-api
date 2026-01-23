package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.time.Instant;

/**
 * Response DTO for category information.
 */
public record CategoryResponse(
    String id,
    String merchantId,
    String catalogId,
    String name,
    String description,
    String imageUrl,
    int displayOrder,
    boolean visible,
    Instant createdAt,
    Instant updatedAt
) {
}