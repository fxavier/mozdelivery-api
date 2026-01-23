package com.xavier.mozdeliveryapi.catalog.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Repository interface for category persistence operations.
 */
public interface CategoryRepository {
    
    /**
     * Save a category.
     */
    Category save(Category category);
    
    /**
     * Find category by ID.
     */
    Optional<Category> findById(CategoryId categoryId);
    
    /**
     * Find all categories for a catalog.
     */
    List<Category> findByCatalogId(CatalogId catalogId);
    
    /**
     * Find visible categories for a catalog (for public browsing).
     */
    List<Category> findVisibleByCatalogId(CatalogId catalogId);
    
    /**
     * Find all categories for a merchant.
     */
    List<Category> findByMerchantId(MerchantId merchantId);
    
    /**
     * Check if category exists.
     */
    boolean existsById(CategoryId categoryId);
    
    /**
     * Check if category belongs to merchant.
     */
    boolean existsByIdAndMerchantId(CategoryId categoryId, MerchantId merchantId);
    
    /**
     * Check if category belongs to catalog.
     */
    boolean existsByIdAndCatalogId(CategoryId categoryId, CatalogId catalogId);
    
    /**
     * Delete category by ID.
     */
    void deleteById(CategoryId categoryId);
}