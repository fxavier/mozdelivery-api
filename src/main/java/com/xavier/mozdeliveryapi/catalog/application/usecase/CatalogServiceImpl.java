package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CatalogRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.exception.CatalogNotFoundException;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of catalog domain service.
 */
@Service
@Transactional
public class CatalogServiceImpl implements CatalogService {
    
    private final CatalogRepository catalogRepository;
    
    public CatalogServiceImpl(CatalogRepository catalogRepository) {
        this.catalogRepository = Objects.requireNonNull(catalogRepository, "Catalog repository cannot be null");
    }
    
    @Override
    public Catalog createCatalog(MerchantId merchantId, String name, String description) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        
        CatalogId catalogId = CatalogId.generate();
        Catalog catalog = new Catalog(catalogId, merchantId, name, description);
        
        return catalogRepository.save(catalog);
    }
    
    @Override
    public Catalog updateCatalog(CatalogId catalogId, String name, String description, Integer displayOrder) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        Catalog catalog = getCatalog(catalogId);
        
        if (name != null || description != null) {
            catalog.updateDetails(
                name != null ? name : catalog.getName(),
                description != null ? description : catalog.getDescription()
            );
        }
        
        if (displayOrder != null) {
            catalog.updateDisplayOrder(displayOrder);
        }
        
        return catalogRepository.save(catalog);
    }
    
    @Override
    public Catalog activateCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        Catalog catalog = getCatalog(catalogId);
        catalog.activate();
        
        return catalogRepository.save(catalog);
    }
    
    @Override
    public Catalog deactivateCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        Catalog catalog = getCatalog(catalogId);
        catalog.deactivate();
        
        return catalogRepository.save(catalog);
    }
    
    @Override
    public Catalog archiveCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        Catalog catalog = getCatalog(catalogId);
        catalog.archive();
        
        return catalogRepository.save(catalog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Catalog getCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        return catalogRepository.findById(catalogId)
            .orElseThrow(() -> new CatalogNotFoundException(catalogId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Catalog> getMerchantCatalogs(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return catalogRepository.findByMerchantId(merchantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Catalog> getMerchantCatalogsByStatus(MerchantId merchantId, CatalogStatus status) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return catalogRepository.findByMerchantIdAndStatus(merchantId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Catalog> getVisibleMerchantCatalogs(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return catalogRepository.findVisibleByMerchantId(merchantId);
    }
    
    @Override
    public void deleteCatalog(CatalogId catalogId) {
        Objects.requireNonNull(catalogId, "Catalog ID cannot be null");
        
        if (!catalogRepository.existsById(catalogId)) {
            throw new CatalogNotFoundException(catalogId);
        }
        
        catalogRepository.deleteById(catalogId);
    }
}