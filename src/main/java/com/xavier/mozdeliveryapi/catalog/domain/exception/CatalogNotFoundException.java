package com.xavier.mozdeliveryapi.catalog.domain.exception;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;

/**
 * Exception thrown when a catalog is not found.
 */
public class CatalogNotFoundException extends RuntimeException {
    
    private final CatalogId catalogId;
    
    public CatalogNotFoundException(CatalogId catalogId) {
        super("Catalog not found: " + catalogId);
        this.catalogId = catalogId;
    }
    
    public CatalogId getCatalogId() {
        return catalogId;
    }
}