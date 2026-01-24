package com.xavier.mozdeliveryapi.catalog.application.dto;

/**
 * Request DTO for updating catalog details.
 */
public record UpdateCatalogRequest(
    String name,
    String description,
    Integer displayOrder
) {
}