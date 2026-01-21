package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Tenant aggregate.
 */
public interface TenantRepository extends Repository<Tenant, TenantId> {
    
    /**
     * Find tenant by name.
     */
    Optional<Tenant> findByName(String name);
    
    /**
     * Find all tenants by vertical.
     */
    List<Tenant> findByVertical(Vertical vertical);
    
    /**
     * Find all tenants by status.
     */
    List<Tenant> findByStatus(TenantStatus status);
    
    /**
     * Find all active tenants.
     */
    List<Tenant> findAllActive();
    
    /**
     * Check if a tenant name already exists.
     */
    boolean existsByName(String name);
    
    /**
     * Count tenants by vertical.
     */
    long countByVertical(Vertical vertical);
    
    /**
     * Count tenants by status.
     */
    long countByStatus(TenantStatus status);
}