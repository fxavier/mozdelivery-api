package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CategoryRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.exception.CategoryNotFoundException;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of CategoryService for category domain operations.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository, "CategoryRepository cannot be null");
    }
    
    @Override
    public Category createCategory(MerchantId merchantId, CatalogId catalogId, String name, String description, int displayOrder) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        
        CategoryId categoryId = CategoryId.generate();
        Category category = new Category(categoryId, merchantId, catalogId, name, description, displayOrder);
        
        return categoryRepository.save(category);
    }
    
    @Override
    public Category updateCategory(CategoryId categoryId, String name, String description, String imageUrl, Integer displayOrder) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        
        if (name != null || description != null || imageUrl != null) {
            category.updateDetails(
                name != null ? name : category.getName(),
                description != null ? description : category.getDescription(),
                imageUrl != null ? imageUrl : category.getImageUrl()
            );
        }
        
        if (displayOrder != null) {
            category.updateDisplayOrder(displayOrder);
        }
        
        return categoryRepository.save(category);
    }
    
    @Override
    public Category showCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        
        category.show();
        return categoryRepository.save(category);
    }
    
    @Override
    public Category hideCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        
        category.hide();
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Category getCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCatalogCategories(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        return categoryRepository.findByCatalogId(catalogId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getVisibleCatalogCategories(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        return categoryRepository.findVisibleByCatalogId(catalogId);
    }
    
    @Override
    public void deleteCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        
        categoryRepository.deleteById(categoryId);
    }
}