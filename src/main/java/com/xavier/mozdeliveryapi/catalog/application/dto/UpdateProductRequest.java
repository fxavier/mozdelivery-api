package com.xavier.mozdeliveryapi.catalog.application.dto;

import java.util.List;

/**
 * Request DTO for updating product details.
 */
public record UpdateProductRequest(
    String name,
    String description,
    List<String> imageUrls
) {
}