package com.xavier.mozdeliveryapi.catalog.domain.exception;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;

/**
 * Exception thrown when a category is not found.
 */
public class CategoryNotFoundException extends RuntimeException {
    
    private final CategoryId categoryId;
    
    public CategoryNotFoundException(CategoryId categoryId) {
        super("Category not found: " + categoryId);
        this.categoryId = categoryId;
    }
    
    public CategoryId getCategoryId() {
        return categoryId;
    }
}