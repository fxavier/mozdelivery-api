package com.xavier.mozdeliveryapi.catalog.infra.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * REST controller for catalog management operations.
 */
@RestController
@RequestMapping("/api/v1/catalogs")
public class CatalogController {
    
    private final CatalogApplicationService catalogApplicationService;
    
    public CatalogController(CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }
    
    /**
     * Create a new catalog.
     */
    @PostMapping
    public ResponseEntity<CatalogResponse> createCatalog(@RequestBody CreateCatalogRequest request) {
        CatalogResponse response = catalogApplicationService.createCatalog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get catalog by ID.
     */
    @GetMapping("/{catalogId}")
    public ResponseEntity<CatalogResponse> getCatalog(@PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.getCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update catalog.
     */
    @PutMapping("/{catalogId}")
    public ResponseEntity<CatalogResponse> updateCatalog(
            @PathVariable String catalogId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer displayOrder) {
        CatalogResponse response = catalogApplicationService.updateCatalog(catalogId, name, description, displayOrder);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activate catalog.
     */
    @PutMapping("/{catalogId}/activate")
    public ResponseEntity<CatalogResponse> activateCatalog(@PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.activateCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deactivate catalog.
     */
    @PutMapping("/{catalogId}/deactivate")
    public ResponseEntity<CatalogResponse> deactivateCatalog(@PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.deactivateCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Archive catalog.
     */
    @PutMapping("/{catalogId}/archive")
    public ResponseEntity<CatalogResponse> archiveCatalog(@PathVariable String catalogId) {
        CatalogResponse response = catalogApplicationService.archiveCatalog(catalogId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete catalog.
     */
    @DeleteMapping("/{catalogId}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable String catalogId) {
        catalogApplicationService.deleteCatalog(catalogId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all catalogs for a merchant.
     */
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<CatalogResponse>> getMerchantCatalogs(@PathVariable String merchantId) {
        List<CatalogResponse> response = catalogApplicationService.getMerchantCatalogs(merchantId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get merchant catalogs by status.
     */
    @GetMapping("/merchant/{merchantId}/status/{status}")
    public ResponseEntity<List<CatalogResponse>> getMerchantCatalogsByStatus(
            @PathVariable String merchantId,
            @PathVariable CatalogStatus status) {
        List<CatalogResponse> response = catalogApplicationService.getMerchantCatalogsByStatus(merchantId, status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get visible catalogs for a merchant (for public browsing).
     */
    @GetMapping("/merchant/{merchantId}/visible")
    public ResponseEntity<List<CatalogResponse>> getVisibleMerchantCatalogs(@PathVariable String merchantId) {
        List<CatalogResponse> response = catalogApplicationService.getVisibleMerchantCatalogs(merchantId);
        return ResponseEntity.ok(response);
    }
}