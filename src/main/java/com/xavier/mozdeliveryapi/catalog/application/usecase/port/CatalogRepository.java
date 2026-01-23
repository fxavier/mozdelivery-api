package com.xavier.mozdeliveryapi.catalog.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Repository interface for catalog persistence operations.
 */
public interface CatalogRepository {
    
    /**
     * Save a catalog.
     */
    Catalog save(Catalog catalog);
    
    /**
     * Find catalog by ID.
     */
    Optional<Catalog> findById(CatalogId catalogId);
    
    /**
     * Find all catalogs for a merchant.
     */
    List<Catalog> findByMerchantId(MerchantId merchantId);
    
    /**
     * Find catalogs by merchant and status.
     */
    List<Catalog> findByMerchantIdAndStatus(MerchantId merchantId, CatalogStatus status);
    
    /**
     * Find visible catalogs for a merchant (for public browsing).
     */
    List<Catalog> findVisibleByMerchantId(MerchantId merchantId);
    
    /**
     * Check if catalog exists.
     */
    boolean existsById(CatalogId catalogId);
    
    /**
     * Check if catalog belongs to merchant.
     */
    boolean existsByIdAndMerchantId(CatalogId catalogId, MerchantId merchantId);
    
    /**
     * Delete catalog by ID.
     */
    void deleteById(CatalogId catalogId);
}