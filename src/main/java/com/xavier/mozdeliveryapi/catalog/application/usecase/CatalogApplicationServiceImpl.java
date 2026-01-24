package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCatalogRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCategoryRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateProductRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.application.mapper.CatalogMapper;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.DomainEventPublisher;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Implementation of catalog application service.
 * This is a simplified implementation focusing on catalog operations.
 */
@Service
@Transactional
public class CatalogApplicationServiceImpl implements CatalogApplicationService {
    
    private final CatalogService catalogService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CatalogMapper catalogMapper;
    private final DomainEventPublisher eventPublisher;
    
    public CatalogApplicationServiceImpl(CatalogService catalogService, CategoryService categoryService, ProductService productService, CatalogMapper catalogMapper, DomainEventPublisher eventPublisher) {
        this.catalogService = Objects.requireNonNull(catalogService, "Catalog service cannot be null");
        this.categoryService = Objects.requireNonNull(categoryService, "Category service cannot be null");
        this.productService = Objects.requireNonNull(productService, "Product service cannot be null");
        this.catalogMapper = Objects.requireNonNull(catalogMapper, "Catalog mapper cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }
    
    @Override
    public CatalogResponse createCatalog(CreateCatalogRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        MerchantId merchantId = MerchantId.of(request.merchantId());
        Catalog catalog = catalogService.createCatalog(merchantId, request.name(), request.description());
        
        if (request.displayOrder() != null) {
            catalog = catalogService.updateCatalog(catalog.getCatalogId(), null, null, request.displayOrder());
        }
        
        // Publish domain events for real-time updates
        publishDomainEvents(catalog);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    public CatalogResponse updateCatalog(String catalogId, String name, String description, Integer displayOrder) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        Catalog catalog = catalogService.updateCatalog(id, name, description, displayOrder);
        
        // Publish domain events for real-time updates
        publishDomainEvents(catalog);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    public CatalogResponse activateCatalog(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        Catalog catalog = catalogService.activateCatalog(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(catalog);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    public CatalogResponse deactivateCatalog(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        Catalog catalog = catalogService.deactivateCatalog(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(catalog);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    public CatalogResponse archiveCatalog(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        Catalog catalog = catalogService.archiveCatalog(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(catalog);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CatalogResponse getCatalog(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        Catalog catalog = catalogService.getCatalog(id);
        
        return catalogMapper.toCatalogResponse(catalog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getMerchantCatalogs(String merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        MerchantId id = MerchantId.of(merchantId);
        List<Catalog> catalogs = catalogService.getMerchantCatalogs(id);
        
        return catalogs.stream()
            .map(catalogMapper::toCatalogResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getMerchantCatalogsByStatus(String merchantId, CatalogStatus status) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        MerchantId id = MerchantId.of(merchantId);
        List<Catalog> catalogs = catalogService.getMerchantCatalogsByStatus(id, status);
        
        return catalogs.stream()
            .map(catalogMapper::toCatalogResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getVisibleMerchantCatalogs(String merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        MerchantId id = MerchantId.of(merchantId);
        List<Catalog> catalogs = catalogService.getVisibleMerchantCatalogs(id);
        
        return catalogs.stream()
            .map(catalogMapper::toCatalogResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteCatalog(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        catalogService.deleteCatalog(id);
    }
    
    // Category operations
    
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        MerchantId merchantId = MerchantId.of(request.merchantId());
        CatalogId catalogId = CatalogId.of(request.catalogId());
        
        Category category = categoryService.createCategory(
            merchantId, 
            catalogId, 
            request.name(), 
            request.description(), 
            request.displayOrder() != null ? request.displayOrder() : 0
        );
        
        if (request.imageUrl() != null) {
            category = categoryService.updateCategory(
                category.getCategoryId(), 
                null, 
                null, 
                request.imageUrl(), 
                null
            );
        }
        
        // Publish domain events for real-time updates
        publishDomainEvents(category);
        
        return catalogMapper.toCategoryResponse(category);
    }
    
    @Override
    public CategoryResponse updateCategory(String categoryId, String name, String description, String imageUrl, Integer displayOrder) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        Category category = categoryService.updateCategory(id, name, description, imageUrl, displayOrder);
        
        // Publish domain events for real-time updates
        publishDomainEvents(category);
        
        return catalogMapper.toCategoryResponse(category);
    }
    
    @Override
    public CategoryResponse showCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        Category category = categoryService.showCategory(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(category);
        
        return catalogMapper.toCategoryResponse(category);
    }
    
    @Override
    public CategoryResponse hideCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        Category category = categoryService.hideCategory(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(category);
        
        return catalogMapper.toCategoryResponse(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        Category category = categoryService.getCategory(id);
        
        return catalogMapper.toCategoryResponse(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCatalogCategories(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        List<Category> categories = categoryService.getCatalogCategories(id);
        
        return categories.stream()
            .map(catalogMapper::toCategoryResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getVisibleCatalogCategories(String catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        CatalogId id = CatalogId.of(catalogId);
        List<Category> categories = categoryService.getVisibleCatalogCategories(id);
        
        return categories.stream()
            .map(catalogMapper::toCategoryResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        categoryService.deleteCategory(id);
    }
    
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        CategoryId categoryId = CategoryId.of(request.categoryId());
        Money price = Money.of(request.price(), Currency.valueOf(request.currency()));
        
        StockInfo stockInfo;
        if (request.trackStock() != null && request.trackStock()) {
            stockInfo = StockInfo.tracked(
                request.currentStock() != null ? request.currentStock() : 0,
                request.lowStockThreshold(),
                request.maxStock()
            );
        } else {
            stockInfo = StockInfo.noTracking();
        }
        
        // Get merchant ID from category (this will be resolved by the service)
        Product product = productService.createProduct(
            null, // MerchantId will be resolved from category
            categoryId,
            request.name(),
            request.description(),
            price,
            stockInfo
        );
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    public ProductResponse updateProduct(String productId, String name, String description, List<String> imageUrls) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(imageUrls, "Image URLs cannot be null");
        
        ProductId id = ProductId.of(productId);
        Product product = productService.updateProduct(id, name, description, imageUrls);
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    public ProductResponse updateProductAvailability(String productId, String availability) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(availability, "Availability cannot be null");
        
        ProductId id = ProductId.of(productId);
        ProductAvailability productAvailability = ProductAvailability.valueOf(availability);
        Product product = productService.updateProductAvailability(id, productAvailability);
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    public ProductResponse updateProductStock(String productId, Integer currentStock, Integer lowStockThreshold, Integer maxStock) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        ProductId id = ProductId.of(productId);
        StockInfo stockInfo;
        
        if (currentStock != null) {
            stockInfo = StockInfo.tracked(currentStock, lowStockThreshold, maxStock);
        } else {
            stockInfo = StockInfo.noTracking();
        }
        
        Product product = productService.updateProductStock(id, stockInfo);
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    public ProductResponse showProduct(String productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        ProductId id = ProductId.of(productId);
        Product product = productService.showProduct(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    public ProductResponse hideProduct(String productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        ProductId id = ProductId.of(productId);
        Product product = productService.hideProduct(id);
        
        // Publish domain events for real-time updates
        publishDomainEvents(product);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(String productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        ProductId id = ProductId.of(productId);
        Product product = productService.getProduct(id);
        
        return catalogMapper.toProductResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getCategoryProducts(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        List<Product> products = productService.getCategoryProducts(id);
        
        return products.stream()
            .map(catalogMapper::toProductResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableCategoryProducts(String categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        CategoryId id = CategoryId.of(categoryId);
        List<Product> products = productService.getAvailableCategoryProducts(id);
        
        return products.stream()
            .map(catalogMapper::toProductResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(String merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        MerchantId id = MerchantId.of(merchantId);
        List<Product> products = productService.getLowStockProducts(id);
        
        return products.stream()
            .map(catalogMapper::toProductResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteProduct(String productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        ProductId id = ProductId.of(productId);
        Product product = productService.getProduct(id); // Get product before deletion for events
        productService.deleteProduct(id);
        
        // Publish domain events
        publishDomainEvents(product);
    }
    
    /**
     * Publish domain events from an aggregate.
     */
    private void publishDomainEvents(Catalog catalog) {
        catalog.getDomainEvents().forEach(eventPublisher::publish);
        catalog.clearDomainEvents();
    }
    
    /**
     * Publish domain events from a category.
     */
    private void publishDomainEvents(Category category) {
        category.getDomainEvents().forEach(eventPublisher::publish);
        category.clearDomainEvents();
    }
    
    /**
     * Publish domain events from a product.
     */
    private void publishDomainEvents(Product product) {
        product.getDomainEvents().forEach(eventPublisher::publish);
        product.clearDomainEvents();
    }
}