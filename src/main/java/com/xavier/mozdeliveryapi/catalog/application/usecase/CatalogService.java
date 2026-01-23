package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Domain service interface for catalog operations.
 */
public interface CatalogService {
    
    /**
     * Create a new catalog for a merchant.
     */
    Catalog createCatalog(MerchantId merchantId, String name, String description);
    
    /**
     * Update catalog details.
     */
    Catalog updateCatalog(CatalogId catalogId, String name, String description, Integer displayOrder);
    
    /**
     * Activate a catalog.
     */
    Catalog activateCatalog(CatalogId catalogId);
    
    /**
     * Deactivate a catalog.
     */
    Catalog deactivateCatalog(CatalogId catalogId);
    
    /**
     * Archive a catalog.
     */
    Catalog archiveCatalog(CatalogId catalogId);
    
    /**
     * Get catalog by ID.
     */
    Catalog getCatalog(CatalogId catalogId);
    
    /**
     * Get all catalogs for a merchant.
     */
    List<Catalog> getMerchantCatalogs(MerchantId merchantId);
    
    /**
     * Get catalogs by status for a merchant.
     */
    List<Catalog> getMerchantCatalogsByStatus(MerchantId merchantId, CatalogStatus status);
    
    /**
     * Get visible catalogs for a merchant (for public browsing).
     */
    List<Catalog> getVisibleMerchantCatalogs(MerchantId merchantId);
    
    /**
     * Delete a catalog.
     */
    void deleteCatalog(CatalogId catalogId);
}