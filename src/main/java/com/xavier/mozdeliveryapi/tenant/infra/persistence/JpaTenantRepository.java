package com.xavier.mozdeliveryapi.tenant.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantStatus;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;

/**
 * JPA repository interface for Tenant entities.
 */
@Repository
public interface JpaTenantRepository extends JpaRepository<TenantEntity, UUID> {
    
    Optional<TenantEntity> findByName(String name);
    
    List<TenantEntity> findByVertical(Vertical vertical);
    
    List<TenantEntity> findByStatus(TenantStatus status);
    
    @Query("SELECT t FROM TenantEntity t WHERE t.status = 'ACTIVE'")
    List<TenantEntity> findAllActive();
    
    boolean existsByName(String name);
    
    long countByVertical(Vertical vertical);
    
    long countByStatus(TenantStatus status);
    
    @Query("SELECT t FROM TenantEntity t WHERE t.id = :tenantId")
    Optional<TenantEntity> findByTenantId(@Param("tenantId") UUID tenantId);
}