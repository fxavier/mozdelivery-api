package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.ProductRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of ProductRepository using JPA.
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    private final JpaProductRepository jpaRepository;
    private final ProductMapper mapper;
    private final CategoryRepositoryHelper categoryHelper;
    
    public ProductRepositoryImpl(JpaProductRepository jpaRepository, ProductMapper mapper, CategoryRepositoryHelper categoryHelper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaProductRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "ProductMapper cannot be null");
        this.categoryHelper = Objects.requireNonNull(categoryHelper, "CategoryRepositoryHelper cannot be null");
    }
    
    @Override
    public Product save(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        
        // Get merchant ID from category
        MerchantId merchantId = categoryHelper.getMerchantIdForCategory(product.getCategoryId());
        
        ProductEntity entity = mapper.toEntity(product, merchantId);
        ProductEntity savedEntity = jpaRepository.save(entity);
        
        return mapper.toDomain(savedEntity, merchantId);
    }
    
    @Override
    public Optional<Product> findById(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        return jpaRepository.findById(productId.value())
            .map(entity -> {
                MerchantId merchantId = categoryHelper.getMerchantIdForCategory(CategoryId.of(entity.getCategoryId()));
                return mapper.toDomain(entity, merchantId);
            });
    }
    
    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        MerchantId merchantId = categoryHelper.getMerchantIdForCategory(categoryId);
        
        return jpaRepository.findByCategoryId(categoryId.value())
            .stream()
            .map(entity -> mapper.toDomain(entity, merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findAvailableByCategoryId(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        MerchantId merchantId = categoryHelper.getMerchantIdForCategory(categoryId);
        
        return jpaRepository.findAvailableByCategoryId(categoryId.value())
            .stream()
            .map(entity -> mapper.toDomain(entity, merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findByMerchantId(merchantId.value())
            .stream()
            .map(entity -> mapper.toDomain(entity, merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByMerchantIdAndAvailability(MerchantId merchantId, ProductAvailability availability) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(availability, "Availability cannot be null");
        
        return jpaRepository.findByMerchantIdAndAvailability(merchantId.value(), availability)
            .stream()
            .map(entity -> mapper.toDomain(entity, merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findLowStockByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findLowStockByMerchantId(merchantId.value())
            .stream()
            .map(entity -> mapper.toDomain(entity, merchantId))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        return jpaRepository.existsById(productId.value());
    }
    
    @Override
    public boolean existsByIdAndMerchantId(ProductId productId, MerchantId merchantId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.existsByIdAndMerchantId(productId.value(), merchantId.value());
    }
    
    @Override
    public boolean existsByIdAndCategoryId(ProductId productId, CategoryId categoryId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return jpaRepository.existsByIdAndCategoryId(productId.value(), categoryId.value());
    }
    
    @Override
    public void deleteById(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        jpaRepository.deleteById(productId.value());
    }
}