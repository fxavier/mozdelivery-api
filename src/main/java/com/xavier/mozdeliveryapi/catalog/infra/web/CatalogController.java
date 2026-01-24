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

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CreateCatalogRequest;
import com.xavier.mozdeliveryapi.catalog.application.usecase.CatalogApplicationService;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for catalog management operations.
 * Provides CRUD operations for catalogs with merchant-specific access controls.
 */
@RestController
@RequestMapping("/api/v1/catalogs")
@Tag(name = "Catalog Management", description = "Catalog management operations for merchants")
public class CatalogController {
    
    private final CatalogApplicationService catalogApplicationService;
    
    public CatalogController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }
    
    /**
     * Create a new catalog.
     */
    @Operation(summary = "Create a new catalog", description = "Create a new catalog for a merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Catalog created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only create their own catalogs")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CatalogResponse> createCatalog(@Valid @RequestBody CreateCatalogRequest request) {
        CatalogResponse response = catalogApplicationService.createCatalog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get catalog by ID.
     */
    @Operation(summary = "Get catalog by ID", description = "Retrieve catalog details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog found"),
        @ApiResponse(responseCode = "404", description = "Catalog not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{catalogId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<CatalogResponse> getCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.getCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update catalog.
     */
    @Operation(summary = "Update catalog", description = "Update catalog name, description, or display order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - merchant can only manage their own catalogs"),
        @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @PutMapping("/{catalogId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CatalogResponse> updateCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId,
            @Parameter(description = "Catalog name") @RequestParam(required = false) String name,
            @Parameter(description = "Catalog description") @RequestParam(required = false) String description,
            @Parameter(description = "Display order") @RequestParam(required = false) Integer displayOrder) {
        CatalogResponse response = catalogApplicationService.updateCatalog(catalogId, name, description, displayOrder);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activate catalog.
     */
    @Operation(summary = "Activate catalog", description = "Make catalog visible to customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog activated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Catalog not found"),
        @ApiResponse(responseCode = "409", description = "Catalog cannot be activated - missing categories")
    })
    @PutMapping("/{catalogId}/activate")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CatalogResponse> activateCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.activateCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deactivate catalog.
     */
    @Operation(summary = "Deactivate catalog", description = "Hide catalog from customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog deactivated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @PutMapping("/{catalogId}/deactivate")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CatalogResponse> deactivateCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.deactivateCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Archive catalog.
     */
    @Operation(summary = "Archive catalog", description = "Archive catalog (permanent hide)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalog archived successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @PutMapping("/{catalogId}/archive")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<CatalogResponse> archiveCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.archiveCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete catalog.
     */
    @Operation(summary = "Delete catalog", description = "Delete a catalog and all its categories/products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Catalog deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Catalog not found"),
        @ApiResponse(responseCode = "409", description = "Catalog cannot be deleted - has active orders")
    })
    @DeleteMapping("/{catalogId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:write')")
    public ResponseEntity<Void> deleteCatalog(
            @Parameter(description = "Catalog ID") @PathVariable String catalogId) {
        catalogApplicationService.deleteCatalog(catalogId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all catalogs for a merchant.
     */
    @Operation(summary = "Get merchant catalogs", description = "Get all catalogs for a specific merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalogs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<List<CatalogResponse>> getMerchantCatalogs(
            @Parameter(description = "Merchant ID") @PathVariable String merchantId) {
        List<CatalogResponse> response = catalogApplicationService.getMerchantCatalogs(merchantId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get merchant catalogs by status.
     */
    @Operation(summary = "Get merchant catalogs by status", description = "Get catalogs for a merchant filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalogs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/merchant/{merchantId}/status/{status}")
    @PreAuthorize("hasAuthority('SCOPE_catalog:read')")
    public ResponseEntity<List<CatalogResponse>> getMerchantCatalogsByStatus(
            @Parameter(description = "Merchant ID") @PathVariable String merchantId,
            @Parameter(description = "Catalog status") @PathVariable CatalogStatus status) {
        List<CatalogResponse> response = catalogApplicationService.getMerchantCatalogsByStatus(merchantId, status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get visible catalogs for a merchant (for public browsing).
     */
    @Operation(summary = "Get visible merchant catalogs", description = "Get visible catalogs for public browsing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visible catalogs retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Merchant not found")
    })
    @GetMapping("/merchant/{merchantId}/visible")
    public ResponseEntity<List<CatalogResponse>> getVisibleMerchantCatalogs(
            @Parameter(description = "Merchant ID") @PathVariable String merchantId) {
        List<CatalogResponse> response = catalogApplicationService.getVisibleMerchantCatalogs(merchantId);
        return ResponseEntity.ok(response);
    }
}