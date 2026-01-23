package com.xavier.mozdeliveryapi.merchant.application.usecase.port;

import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Repository interface for Merchant aggregate.
 */
public interface MerchantRepository extends Repository<Merchant, MerchantId> {
    
    /**
     * Find merchant by business name.
     */
    Optional<Merchant> findByBusinessName(String businessName);
    
    /**
     * Find merchant by contact email.
     */
    Optional<Merchant> findByContactEmail(String contactEmail);
    
    /**
     * Find all merchants by vertical.
     */
    List<Merchant> findByVertical(Vertical vertical);
    
    /**
     * Find all merchants by status.
     */
    List<Merchant> findByStatus(MerchantStatus status);
    
    /**
     * Find all merchants by city.
     */
    List<Merchant> findByCity(String city);
    
    /**
     * Find all merchants by city and vertical.
     */
    List<Merchant> findByCityAndVertical(String city, Vertical vertical);
    
    /**
     * Find all active merchants.
     */
    List<Merchant> findAllActive();
    
    /**
     * Find all publicly visible merchants (approved and active/inactive).
     */
    List<Merchant> findAllPubliclyVisible();
    
    /**
     * Find all publicly visible merchants by city.
     */
    List<Merchant> findPubliclyVisibleByCity(String city);
    
    /**
     * Find all pending approval merchants.
     */
    List<Merchant> findAllPendingApproval();
    
    /**
     * Check if a business name already exists.
     */
    boolean existsByBusinessName(String businessName);
    
    /**
     * Check if a contact email already exists.
     */
    boolean existsByContactEmail(String contactEmail);
    
    /**
     * Count merchants by vertical.
     */
    long countByVertical(Vertical vertical);
    
    /**
     * Count merchants by status.
     */
    long countByStatus(MerchantStatus status);
    
    /**
     * Count merchants by city.
     */
    long countByCity(String city);
}