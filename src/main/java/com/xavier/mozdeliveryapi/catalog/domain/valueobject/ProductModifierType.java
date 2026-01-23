package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

/**
 * Enumeration of product modifier types.
 */
public enum ProductModifierType {
    /**
     * Customer can select only one option (e.g., size).
     */
    SINGLE_CHOICE,
    
    /**
     * Customer can select multiple options (e.g., toppings).
     */
    MULTIPLE_CHOICE
}