package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface for category persistence.
 */
@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    
    /**
     * Find all categories for a catalog.
     */
    List<CategoryEntity> findByCatalogId(UUID catalogId);
    
    /**
     * Find visible categories for a catalog.
     */
    List<CategoryEntity> findByCatalogIdAndIsVisibleTrue(UUID catalogId);
    
    /**
     * Find categories ordered by display order.
     */
    List<CategoryEntity> findByCatalogIdOrderByDisplayOrderAsc(UUID catalogId);
    
    /**
     * Find visible categories ordered by display order.
     */
    List<CategoryEntity> findByCatalogIdAndIsVisibleTrueOrderByDisplayOrderAsc(UUID catalogId);
    
    /**
     * Find categories by merchant ID through catalog relationship.
     */
    @Query("SELECT c FROM CategoryEntity c JOIN CatalogEntity cat ON c.catalogId = cat.id WHERE cat.merchantId = :merchantId ORDER BY c.displayOrder ASC")
    List<CategoryEntity> findByMerchantIdOrderByDisplayOrderAsc(@Param("merchantId") UUID merchantId);
    
    /**
     * Check if category exists for merchant.
     */
    @Query("SELECT COUNT(c) > 0 FROM CategoryEntity c JOIN CatalogEntity cat ON c.catalogId = cat.id WHERE c.id = :categoryId AND cat.merchantId = :merchantId")
    boolean existsByIdAndMerchantId(@Param("categoryId") UUID categoryId, @Param("merchantId") UUID merchantId);
    
    /**
     * Check if category exists for catalog.
     */
    boolean existsByIdAndCatalogId(UUID categoryId, UUID catalogId);
}