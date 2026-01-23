package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Domain service interface for product operations.
 */
public interface ProductService {
    
    /**
     * Create a new product in a category.
     */
    Product createProduct(MerchantId merchantId, CategoryId categoryId, String name, String description, Money price, StockInfo stockInfo);
    
    /**
     * Update product details.
     */
    Product updateProduct(ProductId productId, String name, String description, List<String> imageUrls);
    
    /**
     * Update product availability.
     */
    Product updateProductAvailability(ProductId productId, ProductAvailability availability);
    
    /**
     * Update product stock information.
     */
    Product updateProductStock(ProductId productId, StockInfo stockInfo);
    
    /**
     * Show product to customers.
     */
    Product showProduct(ProductId productId);
    
    /**
     * Hide product from customers.
     */
    Product hideProduct(ProductId productId);
    
    /**
     * Get product by ID.
     */
    Product getProduct(ProductId productId);
    
    /**
     * Get all products for a category.
     */
    List<Product> getCategoryProducts(CategoryId categoryId);
    
    /**
     * Get available products for a category (for public browsing).
     */
    List<Product> getAvailableCategoryProducts(CategoryId categoryId);
    
    /**
     * Get low stock products for a merchant.
     */
    List<Product> getLowStockProducts(MerchantId merchantId);
    
    /**
     * Delete a product.
     */
    void deleteProduct(ProductId productId);
}