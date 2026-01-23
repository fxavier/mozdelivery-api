package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;

/**
 * JPA repository interface for CatalogEntity.
 */
public interface JpaCatalogRepository extends JpaRepository<CatalogEntity, UUID> {
    
    /**
     * Find all catalogs for a merchant.
     */
    List<CatalogEntity> findByMerchantId(UUID merchantId);
    
    /**
     * Find catalogs by merchant and status.
     */
    List<CatalogEntity> findByMerchantIdAndStatus(UUID merchantId, CatalogStatus status);
    
    /**
     * Find visible catalogs for a merchant (for public browsing).
     */
    @Query("SELECT c FROM CatalogEntity c WHERE c.merchantId = :merchantId AND c.status = 'ACTIVE' ORDER BY c.displayOrder ASC, c.name ASC")
    List<CatalogEntity> findVisibleByMerchantId(@Param("merchantId") UUID merchantId);
    
    /**
     * Check if catalog belongs to merchant.
     */
    boolean existsByIdAndMerchantId(UUID catalogId, UUID merchantId);
    
    /**
     * Find catalogs ordered by display order and name.
     */
    List<CatalogEntity> findByMerchantIdOrderByDisplayOrderAscNameAsc(UUID merchantId);
    
    /**
     * Find catalogs by merchant and status ordered by display order and name.
     */
    List<CatalogEntity> findByMerchantIdAndStatusOrderByDisplayOrderAscNameAsc(UUID merchantId, CatalogStatus status);
}