package com.xavier.mozdeliveryapi.catalog.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;

/**
 * Mapper utility for converting between domain objects and DTOs.
 */
@Component
public class CatalogMapper {
    
    /**
     * Convert Catalog domain object to CatalogResponse DTO.
     */
    public CatalogResponse toCatalogResponse(Catalog catalog) {
        List<String> categoryIds = catalog.getCategoryIds().stream()
            .map(Object::toString)
            .collect(Collectors.toList());
            
        return new CatalogResponse(
            catalog.getCatalogId().toString(),
            catalog.getMerchantId().toString(),
            catalog.getName(),
            catalog.getDescription(),
            categoryIds,
            catalog.getStatus(),
            catalog.getDisplayOrder(),
            catalog.isVisible(),
            catalog.getCategoryCount(),
            catalog.getCreatedAt(),
            catalog.getUpdatedAt()
        );
    }
    
    /**
     * Convert Category domain object to CategoryResponse DTO.
     */
    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
            category.getCategoryId().toString(),
            category.getMerchantId().toString(),
            category.getCatalogId().toString(),
            category.getName(),
            category.getDescription(),
            category.getImageUrl(),
            category.getDisplayOrder(),
            category.isVisible(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
    
    /**
     * Convert Product domain object to ProductResponse DTO.
     */
    public ProductResponse toProductResponse(Product product) {
        ProductResponse.StockInfoResponse stockInfoResponse = new ProductResponse.StockInfoResponse(
            product.getStockInfo().trackStock(),
            product.getStockInfo().currentStock(),
            product.getStockInfo().lowStockThreshold(),
            product.getStockInfo().maxStock(),
            product.isLowStock(),
            product.getStockInfo().hasStock()
        );
        
        return new ProductResponse(
            product.getProductId().toString(),
            product.getMerchantId().toString(),
            product.getCategoryId().toString(),
            product.getName(),
            product.getDescription(),
            product.getImageUrls(),
            product.getPrice().amount(),
            product.getCurrency().getCode(),
            product.getAvailability(),
            product.isVisible(),
            product.canBeOrdered(),
            stockInfoResponse,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}