package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCatalogRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCategoryRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateProductRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;

/**
 * Application service interface for catalog management operations.
 */
public interface CatalogApplicationService {
    
    // Catalog operations
    CatalogResponse createCatalog(CreateCatalogRequest request);
    CatalogResponse updateCatalog(String catalogId, String name, String description, Integer displayOrder);
    CatalogResponse activateCatalog(String catalogId);
    CatalogResponse deactivateCatalog(String catalogId);
    CatalogResponse archiveCatalog(String catalogId);
    CatalogResponse getCatalog(String catalogId);
    List<CatalogResponse> getMerchantCatalogs(String merchantId);
    List<CatalogResponse> getMerchantCatalogsByStatus(String merchantId, CatalogStatus status);
    List<CatalogResponse> getVisibleMerchantCatalogs(String merchantId);
    void deleteCatalog(String catalogId);
    
    // Category operations
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse updateCategory(String categoryId, String name, String description, String imageUrl, Integer displayOrder);
    CategoryResponse showCategory(String categoryId);
    CategoryResponse hideCategory(String categoryId);
    CategoryResponse getCategory(String categoryId);
    List<CategoryResponse> getCatalogCategories(String catalogId);
    List<CategoryResponse> getVisibleCatalogCategories(String catalogId);
    void deleteCategory(String categoryId);
    
    // Product operations
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(String productId, String name, String description, List<String> imageUrls);
    ProductResponse updateProductAvailability(String productId, String availability);
    ProductResponse updateProductStock(String productId, Integer currentStock, Integer lowStockThreshold, Integer maxStock);
    ProductResponse showProduct(String productId);
    ProductResponse hideProduct(String productId);
    ProductResponse getProduct(String productId);
    List<ProductResponse> getCategoryProducts(String categoryId);
    List<ProductResponse> getAvailableCategoryProducts(String categoryId);
    List<ProductResponse> getLowStockProducts(String merchantId);
    void deleteProduct(String productId);
}