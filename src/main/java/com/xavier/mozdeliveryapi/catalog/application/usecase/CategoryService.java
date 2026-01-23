package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain service interface for category operations.
 */
public interface CategoryService {
    
    /**
     * Create a new category in a catalog.
     */
    Category createCategory(MerchantId merchantId, CatalogId catalogId, String name, String description, int displayOrder);
    
    /**
     * Update category details.
     */
    Category updateCategory(CategoryId categoryId, String name, String description, String imageUrl, Integer displayOrder);
    
    /**
     * Show category to customers.
     */
    Category showCategory(CategoryId categoryId);
    
    /**
     * Hide category from customers.
     */
    Category hideCategory(CategoryId categoryId);
    
    /**
     * Get category by ID.
     */
    Category getCategory(CategoryId categoryId);
    
    /**
     * Get all categories for a catalog.
     */
    List<Category> getCatalogCategories(CatalogId catalogId);
    
    /**
     * Get visible categories for a catalog (for public browsing).
     */
    List<Category> getVisibleCatalogCategories(CatalogId catalogId);
    
    /**
     * Delete a category.
     */
    void deleteCategory(CategoryId categoryId);
}