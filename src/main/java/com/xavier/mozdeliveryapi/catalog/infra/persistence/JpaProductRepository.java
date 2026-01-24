package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;

/**
 * JPA repository interface for product persistence.
 */
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    
    /**
     * Find all products for a category.
     */
    List<ProductEntity> findByCategoryId(UUID categoryId);
    
    /**
     * Find available products for a category (for public browsing).
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.categoryId = :categoryId AND p.isVisible = true AND p.availability = 'AVAILABLE'")
    List<ProductEntity> findAvailableByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * Find all products for categories belonging to a merchant.
     */
    @Query("SELECT p FROM ProductEntity p " +
           "JOIN CategoryEntity c ON p.categoryId = c.id " +
           "JOIN CatalogEntity cat ON c.catalogId = cat.id " +
           "WHERE cat.merchantId = :merchantId")
    List<ProductEntity> findByMerchantId(@Param("merchantId") UUID merchantId);
    
    /**
     * Find products by merchant and availability.
     */
    @Query("SELECT p FROM ProductEntity p " +
           "JOIN CategoryEntity c ON p.categoryId = c.id " +
           "JOIN CatalogEntity cat ON c.catalogId = cat.id " +
           "WHERE cat.merchantId = :merchantId AND p.availability = :availability")
    List<ProductEntity> findByMerchantIdAndAvailability(@Param("merchantId") UUID merchantId, 
                                                       @Param("availability") ProductAvailability availability);
    
    /**
     * Find low stock products for a merchant.
     * Products are considered low stock if they track stock and current stock <= low stock threshold.
     */
    @Query(value = "SELECT p.* FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "JOIN catalogs cat ON c.catalog_id = cat.id " +
           "WHERE cat.merchant_id = :merchantId " +
           "AND p.stock_info IS NOT NULL " +
           "AND (p.stock_info ->> 'trackStock')::boolean = true " +
           "AND (p.stock_info ->> 'lowStockThreshold') IS NOT NULL " +
           "AND (p.stock_info ->> 'currentStock')::int <= (p.stock_info ->> 'lowStockThreshold')::int",
           nativeQuery = true)
    List<ProductEntity> findLowStockByMerchantId(@Param("merchantId") UUID merchantId);
    
    /**
     * Check if product belongs to merchant.
     */
    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p " +
           "JOIN CategoryEntity c ON p.categoryId = c.id " +
           "JOIN CatalogEntity cat ON c.catalogId = cat.id " +
           "WHERE p.id = :productId AND cat.merchantId = :merchantId")
    boolean existsByIdAndMerchantId(@Param("productId") UUID productId, @Param("merchantId") UUID merchantId);
    
    /**
     * Check if product belongs to category.
     */
    boolean existsByIdAndCategoryId(UUID productId, UUID categoryId);
}
