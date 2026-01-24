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

import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCategoryRequest;
import com.xavier.mozdeliveryapi.catalog.application.usecase.CatalogApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for category management operations.
 * Provides CRUD operations for categories with merchant-specific access controls.
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "Category management operations for merchants")
public class CategoryController {
    
    private final CatalogApplicationService catalogApplicationService;
    
    public CategoryController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }
    
    /**
     * Create a new category.
     */
    @Operation(summary = "Create a new category", description = "Create a new category within a catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only manage their own categories"),
        @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = catalogApplicationService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get category by ID.
     */
    @Operation(summary = "Get category by ID", description = "Retrieve category details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        CategoryResponse response = catalogApplicationService.getCategory(categoryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update category details.
     */
    @Operation(summary = "Update category", description = "Update category name, description, image, or display order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only manage their own categories"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId,
            @Parameter(description = "Category name") @RequestParam(required = false) String name,
            @Parameter(description = "Category description") @RequestParam(required = false) String description,
            @Parameter(description = "Category image URL") @RequestParam(required = false) String imageUrl,
            @Parameter(description = "Display order") @RequestParam(required = false) Integer displayOrder) {
        CategoryResponse response = catalogApplicationService.updateCategory(categoryId, name, description, imageUrl, displayOrder);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Show category to customers.
     */
    @Operation(summary = "Show category", description = "Make category visible to customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category shown successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{categoryId}/show")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CategoryResponse> showCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        CategoryResponse response = catalogApplicationService.showCategory(categoryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Hide category from customers.
     */
    @Operation(summary = "Hide category", description = "Hide category from customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category hidden successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{categoryId}/hide")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CategoryResponse> hideCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        CategoryResponse response = catalogApplicationService.hideCategory(categoryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete category.
     */
    @Operation(summary = "Delete category", description = "Delete a category and all its products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "409", description = "Category cannot be deleted - has active products")
    })
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {
        catalogApplicationService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all categories for a catalog.
     */
    @Operation(summary = "Get catalog categories", description = "Get all categories for a specific catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Catalog not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/catalog/{catalogId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<List<CategoryResponse>> getCatalogCategories(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        List<CategoryResponse> response = catalogApplicationService.getCatalogCategories(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get visible categories for a catalog (public endpoint).
     */
    @Operation(summary = "Get visible catalog categories", description = "Get visible categories for public browsing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visible categories retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @GetMapping("/catalog/{catalogId}/visible")
    public ResponseEntity<List<CategoryResponse>> getVisibleCatalogCategories(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        List<CategoryResponse> response = catalogApplicationService.getVisibleCatalogCategories(catalogId);
        return ResponseEntity.ok(response);
    }
}