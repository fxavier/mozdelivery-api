package com.xavier.mozdeliveryapi.catalog.infra.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.application.usecase.CatalogApplicationService;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantApplicationService;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for public browsing APIs (no authentication required).
 * Provides merchant discovery, catalog browsing, and location-based filtering.
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
@Tag(name = "Public Browsing", description = "Public APIs for merchant discovery and catalog browsing")
public class PublicBrowsingController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicBrowsingController.class);
    
    private final MerchantApplicationService merchantService;
    private final CatalogApplicationService catalogService;
    
    public PublicBrowsingController(
            MerchantApplicationService merchantService,
            CatalogApplicationService catalogService) {
        this.merchantService = merchantService;
        this.catalogService = catalogService;
    }
    
    // ========== Merchant Discovery APIs ==========
    
    /**
     * Get all publicly visible merchants with optional filtering.
     */
    @Operation(summary = "Discover merchants", description = "Get all publicly visible merchants with optional city and vertical filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Merchants retrieved successfully")
    })
    @GetMapping("/merchants")
    public ResponseEntity<List<MerchantResponse>> discoverMerchants(
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by vertical") @RequestParam(required = false) Vertical vertical) {
        
        logger.debug("Discovering merchants - City: {}, Vertical: {}", city, vertical);
        
        List<MerchantResponse> merchants;
        
        if (city != null && vertical != null) {
            merchants = merchantService.getMerchantsByCityAndVertical(city, vertical);
        } else if (city != null) {
            merchants = merchantService.getMerchantsByCity(city);
        } else {
            merchants = merchantService.getAllPublicMerchants();
        }
        
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchant by ID (public information only).
     */
    @Operation(summary = "Get merchant details", description = "Get public merchant information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Merchant found"),
        @ApiResponse(responseCode = "404", description = "Merchant not found or not publicly visible")
    })
    @GetMapping("/merchants/{merchantId}")
    public ResponseEntity<MerchantResponse> getMerchant(@PathVariable String merchantId) {
        logger.debug("Getting public merchant info: {}", merchantId);
        
        return merchantService.getPublicMerchant(merchantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get merchants by city.
     */
    @Operation(summary = "Get merchants by city", description = "Get all publicly visible merchants in a specific city")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Merchants retrieved successfully")
    })
    @GetMapping("/merchants/city/{city}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByCity(@PathVariable String city) {
        logger.debug("Getting merchants for city: {}", city);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByCity(city);
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchants by city and vertical.
     */
    @Operation(summary = "Get merchants by city and vertical", description = "Get merchants filtered by city and business vertical")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Merchants retrieved successfully")
    })
    @GetMapping("/merchants/city/{city}/vertical/{vertical}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByCityAndVertical(
            @PathVariable String city,
            @PathVariable Vertical vertical) {
        
        logger.debug("Getting merchants for city: {} and vertical: {}", city, vertical);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByCityAndVertical(city, vertical);
        return ResponseEntity.ok(merchants);
    }
    
    // ========== Catalog Browsing APIs ==========
    
    /**
     * Get visible catalogs for a merchant.
     */
    @Operation(summary = "Get merchant catalogs", description = "Get all visible catalogs for a specific merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalogs retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Merchant not found")
    })
    @GetMapping("/merchants/{merchantId}/catalogs")
    public ResponseEntity<List<CatalogResponse>> getMerchantCatalogs(@PathVariable String merchantId) {
        logger.debug("Getting visible catalogs for merchant: {}", merchantId);
        
        // First verify merchant exists and is publicly visible
        if (merchantService.getPublicMerchant(merchantId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<CatalogResponse> catalogs = catalogService.getVisibleMerchantCatalogs(merchantId);
        return ResponseEntity.ok(catalogs);
    }
    
    /**
     * Get catalog details.
     */
    @Operation(summary = "Get catalog details", description = "Get catalog information including categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog found"),
        @ApiResponse(responseCode = "404", description = "Catalog not found or not visible")
    })
    @GetMapping("/catalogs/{catalogId}")
    public ResponseEntity<CatalogResponse> getCatalog(@PathVariable String catalogId) {
        logger.debug("Getting catalog: {}", catalogId);
        
        try {
            CatalogResponse catalog = catalogService.getCatalog(catalogId);
            // Only return if catalog is visible (active status)
            if (catalog.status().name().equals("ACTIVE")) {
                return ResponseEntity.ok(catalog);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.debug("Catalog not found or not accessible: {}", catalogId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get visible categories for a catalog.
     */
    @Operation(summary = "Get catalog categories", description = "Get all visible categories in a catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Catalog not found or not visible")
    })
    @GetMapping("/catalogs/{catalogId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCatalogCategories(@PathVariable String catalogId) {
        logger.debug("Getting visible categories for catalog: {}", catalogId);
        
        try {
            // First verify catalog exists and is visible
            CatalogResponse catalog = catalogService.getCatalog(catalogId);
            if (!catalog.status().name().equals("ACTIVE")) {
                return ResponseEntity.notFound().build();
            }
            
            List<CategoryResponse> categories = catalogService.getVisibleCatalogCategories(catalogId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.debug("Catalog not found or not accessible: {}", catalogId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get category details.
     */
    @Operation(summary = "Get category details", description = "Get category information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found or not visible")
    })
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable String categoryId) {
        logger.debug("Getting category: {}", categoryId);
        
        try {
            CategoryResponse category = catalogService.getCategory(categoryId);
            // Only return if category is visible
            if (category.visible()) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.debug("Category not found or not accessible: {}", categoryId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get available products for a category.
     */
    @Operation(summary = "Get category products", description = "Get all available products in a category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found or not visible")
    })
    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<List<ProductResponse>> getCategoryProducts(@PathVariable String categoryId) {
        logger.debug("Getting available products for category: {}", categoryId);
        
        try {
            // First verify category exists and is visible
            CategoryResponse category = catalogService.getCategory(categoryId);
            if (!category.visible()) {
                return ResponseEntity.notFound().build();
            }
            
            List<ProductResponse> products = catalogService.getAvailableCategoryProducts(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.debug("Category not found or not accessible: {}", categoryId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get product details.
     */
    @Operation(summary = "Get product details", description = "Get product information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found or not available")
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        logger.debug("Getting product: {}", productId);
        
        try {
            ProductResponse product = catalogService.getProduct(productId);
            // Only return if product is available
            if (product.availability().name().equals("AVAILABLE")) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.debug("Product not found or not accessible: {}", productId);
            return ResponseEntity.notFound().build();
        }
    }
}