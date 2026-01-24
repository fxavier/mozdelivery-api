package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Helper component to resolve merchant ID from category relationships.
 */
@Component
public class CategoryRepositoryHelper {
    
    private final JpaCategoryRepository categoryRepository;
    private final JpaCatalogRepository catalogRepository;
    
    public CategoryRepositoryHelper(JpaCategoryRepository categoryRepository, JpaCatalogRepository catalogRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository, "JpaCategoryRepository cannot be null");
        this.catalogRepository = Objects.requireNonNull(catalogRepository, "JpaCatalogRepository cannot be null");
    }
    
    /**
     * Get merchant ID for a given category.
     */
    public MerchantId getMerchantIdForCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId.value())
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        CatalogEntity catalogEntity = catalogRepository.findById(categoryEntity.getCatalogId())
            .orElseThrow(() -> new IllegalArgumentException("Catalog not found for category: " + categoryId));
        
        return MerchantId.of(catalogEntity.getMerchantId());
    }
    
    /**
     * Get merchant ID for a given catalog.
     */
    public MerchantId getMerchantIdForCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogEntity catalogEntity = catalogRepository.findById(catalogId.value())
            .orElseThrow(() -> new IllegalArgumentException("Catalog not found: " + catalogId));
        
        return MerchantId.of(catalogEntity.getMerchantId());
    }
}