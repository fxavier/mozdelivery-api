package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

/**
 * Enumeration of catalog statuses.
 */
public enum CatalogStatus {
    /**
     * Catalog is being created or edited and not visible to customers.
     */
    DRAFT,
    
    /**
     * Catalog is active and visible to customers.
     */
    ACTIVE,
    
    /**
     * Catalog is temporarily hidden from customers but can be reactivated.
     */
    INACTIVE,
    
    /**
     * Catalog is archived and cannot be reactivated.
     */
    ARCHIVED;
    
    /**
     * Check if catalog is visible to customers.
     */
    public boolean isVisible() {
        return this == ACTIVE;
    }
    
    /**
     * Check if catalog can be edited.
     */
    public boolean canBeEdited() {
        return this == DRAFT || this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * Check if catalog can be activated.
     */
    public boolean canBeActivated() {
        return this == DRAFT || this == INACTIVE;
    }
    
    /**
     * Check if catalog can be deactivated.
     */
    public boolean canBeDeactivated() {
        return this == ACTIVE;
    }
}