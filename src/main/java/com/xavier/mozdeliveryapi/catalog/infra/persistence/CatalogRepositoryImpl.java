package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CatalogRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of CatalogRepository using JPA.
 */
@Component
public class CatalogRepositoryImpl implements CatalogRepository {
    
    private final JpaCatalogRepository jpaRepository;
    
    public CatalogRepositoryImpl(JpaCatalogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Catalog save(Catalog catalog) {
        CatalogEntity entity = jpaRepository.findById(catalog.getCatalogId().value())
            .map(existing -> {
                existing.updateFrom(catalog);
                return existing;
            })
            .orElse(new CatalogEntity(catalog));
        
        CatalogEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Catalog> findById(CatalogId catalogId) {
        return jpaRepository.findById(catalogId.value())
            .map(CatalogEntity::toDomain);
    }
    
    @Override
    public List<Catalog> findByMerchantId(MerchantId merchantId) {
        return jpaRepository.findByMerchantIdOrderByDisplayOrderAscNameAsc(merchantId.value())
            .stream()
            .map(CatalogEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Catalog> findByMerchantIdAndStatus(MerchantId merchantId, CatalogStatus status) {
        return jpaRepository.findByMerchantIdAndStatusOrderByDisplayOrderAscNameAsc(merchantId.value(), status)
            .stream()
            .map(CatalogEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Catalog> findVisibleByMerchantId(MerchantId merchantId) {
        return jpaRepository.findVisibleByMerchantId(merchantId.value())
            .stream()
            .map(CatalogEntity::toDomain)
            .toList();
    }
    
    @Override
    public boolean existsById(CatalogId catalogId) {
        return jpaRepository.existsById(catalogId.value());
    }
    
    @Override
    public boolean existsByIdAndMerchantId(CatalogId catalogId, MerchantId merchantId) {
        return jpaRepository.existsByIdAndMerchantId(catalogId.value(), merchantId.value());
    }
    
    @Override
    public void deleteById(CatalogId catalogId) {
        jpaRepository.deleteById(catalogId.value());
    }
}