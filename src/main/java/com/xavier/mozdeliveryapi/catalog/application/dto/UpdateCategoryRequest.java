package com.xavier.mozdeliveryapi.catalog.application.dto;

/**
 * Request DTO for updating category details.
 */
public record UpdateCategoryRequest(
    String name,
    String description,
    String imageUrl,
    Integer displayOrder
) {
}