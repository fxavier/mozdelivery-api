package com.xavier.mozdeliveryapi.merchant.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * JPA repository interface for MerchantEntity.
 */
@Repository
public interface JpaMerchantRepository extends JpaRepository<MerchantEntity, UUID> {
    
    Optional<MerchantEntity> findByBusinessName(String businessName);
    
    Optional<MerchantEntity> findByContactEmail(String contactEmail);
    
    List<MerchantEntity> findByVertical(Vertical vertical);
    
    List<MerchantEntity> findByStatus(MerchantStatus status);
    
    List<MerchantEntity> findByCity(String city);
    
    List<MerchantEntity> findByCityAndVertical(String city, Vertical vertical);
    
    @Query("SELECT m FROM MerchantEntity m WHERE m.status = 'ACTIVE'")
    List<MerchantEntity> findAllActive();
    
    @Query("SELECT m FROM MerchantEntity m WHERE m.status IN ('ACTIVE', 'INACTIVE')")
    List<MerchantEntity> findAllPubliclyVisible();
    
    @Query("SELECT m FROM MerchantEntity m WHERE m.city = :city AND m.status IN ('ACTIVE', 'INACTIVE')")
    List<MerchantEntity> findPubliclyVisibleByCity(@Param("city") String city);
    
    @Query("SELECT m FROM MerchantEntity m WHERE m.status = 'PENDING'")
    List<MerchantEntity> findAllPendingApproval();
    
    boolean existsByBusinessName(String businessName);
    
    boolean existsByContactEmail(String contactEmail);
    
    long countByVertical(Vertical vertical);
    
    long countByStatus(MerchantStatus status);
    
    long countByCity(String city);
}