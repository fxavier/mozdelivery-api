package com.xavier.mozdeliveryapi.catalog.infra.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.catalog.application.dto.CreateProductRequest;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.application.usecase.CatalogApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for product management operations.
 * Provides CRUD operations for products with merchant-specific access controls.
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "Product management operations for merchants")
public class ProductController {
    
    private final CatalogApplicationService catalogApplicationService;
    
    public ProductController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }
    
    /**
     * Create a new product.
     */
    @Operation(summary = "Create a new product", description = "Create a new product within a category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only manage their own products"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = catalogApplicationService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get product by ID.
     */
    @Operation(summary = "Get product by ID", description = "Retrieve product details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "Product ID") @PathVariable String productId) {
        ProductResponse response = catalogApplicationService.getProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update product details.
     */
    @Operation(summary = "Update product", description = "Update product name, description, or images")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only manage their own products"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String productId,
            @Parameter(description = "Product name") @RequestParam(required = false) String name,
            @Parameter(description = "Product description") @RequestParam(required = false) String description,
            @Parameter(description = "Product image URLs") @RequestParam(required = false) List<String> imageUrls) {
        ProductResponse response = catalogApplicationService.updateProduct(productId, name, description, imageUrls);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update product availability.
     */
    @Operation(summary = "Update product availability", description = "Update product availability status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product availability updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid availability status"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}/availability")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> updateProductAvailability(
            @Parameter(description = "Product ID") @PathVariable String productId,
            @Parameter(description = "Availability status") @RequestParam String availability) {
        ProductResponse response = catalogApplicationService.updateProductAvailability(productId, availability);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update product stock information.
     */
    @Operation(summary = "Update product stock", description = "Update product stock levels and thresholds")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product stock updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid stock data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}/stock")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> updateProductStock(
            @Parameter(description = "Product ID") @PathVariable String productId,
            @Parameter(description = "Current stock") @RequestParam(required = false) Integer currentStock,
            @Parameter(description = "Low stock threshold") @RequestParam(required = false) Integer lowStockThreshold,
            @Parameter(description = "Maximum stock") @RequestParam(required = false) Integer maxStock) {
        ProductResponse response = catalogApplicationService.updateProductStock(productId, currentStock, lowStockThreshold, maxStock);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Show product to customers.
     */
    @Operation(summary = "Show product", description = "Make product visible to customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product shown successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}/show")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> showProduct(
            @Parameter(description = "Product ID") @PathVariable String productId) {
        ProductResponse response = catalogApplicationService.showProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Hide product from customers.
     */
    @Operation(summary = "Hide product", description = "Hide product from customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product hidden successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}/hide")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<ProductResponse> hideProduct(
            @Parameter(description = "Product ID") @PathVariable String productId) {
        ProductResponse response = catalogApplicationService.hideProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete product.
     */
    @Operation(summary = "Delete product", description = "Delete a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "Product cannot be deleted - has active orders")
    })
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String productId) {
        catalogApplicationService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all products for a category.
     */
    @Operation(summary = "Get category products", description = "Get all products for a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<List<ProductResponse>> getCategoryProducts(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        List<ProductResponse> response = catalogApplicationService.getCategoryProducts(categoryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get available products for a category (public endpoint).
     */
    @Operation(summary = "Get available category products", description = "Get available products for public browsing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/category/{categoryId}/available")
    public ResponseEntity<List<ProductResponse>> getAvailableCategoryProducts(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        List<ProductResponse> response = catalogApplicationService.getAvailableCategoryProducts(categoryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get low stock products for a merchant.
     */
    @Operation(summary = "Get low stock products", description = "Get products with low stock levels for a merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Low stock products retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/merchant/{merchantId}/low-stock")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @Parameter(description = "Merchant ID") @PathVariable String merchantId) {
        List<ProductResponse> response = catalogApplicationService.getLowStockProducts(merchantId);
        return ResponseEntity.ok(response);
    }
}