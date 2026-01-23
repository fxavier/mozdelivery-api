package com.xavier.mozdeliveryapi.catalog.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Repository interface for product persistence operations.
 */
public interface ProductRepository {
    
    /**
     * Save a product.
     */
    Product save(Product product);
    
    /**
     * Find product by ID.
     */
    Optional<Product> findById(ProductId productId);
    
    /**
     * Find all products for a category.
     */
    List<Product> findByCategoryId(CategoryId categoryId);
    
    /**
     * Find available products for a category (for public browsing).
     */
    List<Product> findAvailableByCategoryId(CategoryId categoryId);
    
    /**
     * Find all products for a merchant.
     */
    List<Product> findByMerchantId(MerchantId merchantId);
    
    /**
     * Find products by merchant and availability.
     */
    List<Product> findByMerchantIdAndAvailability(MerchantId merchantId, ProductAvailability availability);
    
    /**
     * Find low stock products for a merchant.
     */
    List<Product> findLowStockByMerchantId(MerchantId merchantId);
    
    /**
     * Check if product exists.
     */
    boolean existsById(ProductId productId);
    
    /**
     * Check if product belongs to merchant.
     */
    boolean existsByIdAndMerchantId(ProductId productId, MerchantId merchantId);
    
    /**
     * Check if product belongs to category.
     */
    boolean existsByIdAndCategoryId(ProductId productId, CategoryId categoryId);
    
    /**
     * Delete product by ID.
     */
    void deleteById(ProductId productId);
}