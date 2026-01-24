package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CategoryRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * JPA implementation of CategoryRepository.
 */
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    
    private final JpaCategoryRepository jpaRepository;
    private final CategoryRepositoryHelper categoryHelper;
    
    public CategoryRepositoryImpl(JpaCategoryRepository jpaRepository, CategoryRepositoryHelper categoryHelper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaCategoryRepository cannot be null");
        this.categoryHelper = Objects.requireNonNull(categoryHelper, "CategoryRepositoryHelper cannot be null");
    }
    
    @Override
    public Category save(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");
        
        CategoryEntity entity = jpaRepository.findById(category.getCategoryId().value())
            .map(existing -> {
                existing.updateFrom(category);
                return existing;
            })
            .orElse(new CategoryEntity(category));
        
        CategoryEntity saved = jpaRepository.save(entity);
        return saved.toDomain(category.getMerchantId());
    }
    
    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return jpaRepository.findById(categoryId.value())
            .map(entity -> {
                // Get merchant ID from catalog
                MerchantId merchantId = categoryHelper.getMerchantIdForCategory(categoryId);
                return entity.toDomain(merchantId);
            });
    }
    
    @Override
    public List<Category> findByCatalogId(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        MerchantId merchantId = categoryHelper.getMerchantIdForCatalog(catalogId);
        
        return jpaRepository.findByCatalogIdOrderByDisplayOrderAsc(catalogId.value())
            .stream()
            .map(entity -> entity.toDomain(merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findVisibleByCatalogId(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        MerchantId merchantId = categoryHelper.getMerchantIdForCatalog(catalogId);
        
        return jpaRepository.findByCatalogIdAndIsVisibleTrueOrderByDisplayOrderAsc(catalogId.value())
            .stream()
            .map(entity -> entity.toDomain(merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findByMerchantIdOrderByDisplayOrderAsc(merchantId.value())
            .stream()
            .map(entity -> entity.toDomain(merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return jpaRepository.existsById(categoryId.value());
    }
    
    @Override
    public boolean existsByIdAndMerchantId(CategoryId categoryId, MerchantId merchantId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.existsByIdAndMerchantId(categoryId.value(), merchantId.value());
    }
    
    @Override
    public boolean existsByIdAndCatalogId(CategoryId categoryId, CatalogId catalogId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        return jpaRepository.existsByIdAndCatalogId(categoryId.value(), catalogId.value());
    }
    
    @Override
    public void deleteById(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        jpaRepository.deleteById(categoryId.value());
    }
}