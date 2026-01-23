package com.xavier.mozdeliveryapi.tenant.infra.persistence;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import com.xavier.mozdeliveryapi.tenant.application.usecase.port.TenantRepository;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantStatus;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;

/**
 * Implementation of TenantRepository using JPA.
 */
@Component
public class TenantRepositoryImpl implements TenantRepository {
    
    private final JpaTenantRepository jpaRepository;
    
    public TenantRepositoryImpl(JpaTenantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Tenant save(Tenant tenant) {
        Optional<TenantEntity> existingEntity = jpaRepository.findById(tenant.getTenantId().value());
        
        TenantEntity entity;
        if (existingEntity.isPresent()) {
            entity = existingEntity.get();
            entity.updateFrom(tenant);
        } else {
            entity = new TenantEntity(tenant);
        }
        
        TenantEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }
    
    @Override
    public Optional<Tenant> findById(TenantId tenantId) {
        return jpaRepository.findById(tenantId.value())
            .map(TenantEntity::toDomain);
    }
    
    @Override
    public void delete(Tenant tenant) {
        jpaRepository.deleteById(tenant.getTenantId().value());
    }
    
    @Override
    public boolean existsById(TenantId tenantId) {
        return jpaRepository.existsById(tenantId.value());
    }
    
    @Override
    public Optional<Tenant> findByName(String name) {
        return jpaRepository.findByName(name)
            .map(TenantEntity::toDomain);
    }
    
    @Override
    public List<Tenant> findByVertical(Vertical vertical) {
        return jpaRepository.findByVertical(vertical)
            .stream()
            .map(TenantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Tenant> findByStatus(TenantStatus status) {
        return jpaRepository.findByStatus(status)
            .stream()
            .map(TenantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Tenant> findAllActive() {
        return jpaRepository.findAllActive()
            .stream()
            .map(TenantEntity::toDomain)
            .toList();
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
    
    @Override
    public long countByVertical(Vertical vertical) {
        return jpaRepository.countByVertical(vertical);
    }
    
    @Override
    public long countByStatus(TenantStatus status) {
        return jpaRepository.countByStatus(status);
    }
}
