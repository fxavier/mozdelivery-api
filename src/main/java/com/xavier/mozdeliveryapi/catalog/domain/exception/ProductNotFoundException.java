package com.xavier.mozdeliveryapi.catalog.domain.exception;

import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;

/**
 * Exception thrown when a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {
    
    private final ProductId productId;
    
    public ProductNotFoundException(ProductId productId) {
        super("Product not found: " + productId);
        this.productId = productId;
    }
    
    public ProductId getProductId() {
        return productId;
    }
}