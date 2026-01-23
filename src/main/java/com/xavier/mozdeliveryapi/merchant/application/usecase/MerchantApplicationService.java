package com.xavier.mozdeliveryapi.merchant.application.usecase;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Application service for merchant operations.
 */
@Service
@Transactional(readOnly = true)
public class MerchantApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantApplicationService.class);
    
    private final MerchantRepository merchantRepository;
    
    public MerchantApplicationService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
    
    /**
     * Get merchant by ID.
     */
    public Optional<MerchantResponse> getMerchant(String merchantId) {
        logger.debug("Retrieving merchant: {}", merchantId);
        
        return merchantRepository.findById(MerchantId.of(merchantId))
            .map(MerchantResponse::from);
    }
    
    /**
     * Get public merchant information (for guest browsing).
     */
    public Optional<MerchantResponse> getPublicMerchant(String merchantId) {
        logger.debug("Retrieving public merchant info: {}", merchantId);
        
        return merchantRepository.findById(MerchantId.of(merchantId))
            .filter(Merchant::isPubliclyVisible)
            .map(MerchantResponse::publicFrom);
    }
    
    /**
     * Get all merchants by city (public view).
     */
    public List<MerchantResponse> getMerchantsByCity(String city) {
        logger.debug("Retrieving merchants for city: {}", city);
        
        return merchantRepository.findPubliclyVisibleByCity(city).stream()
            .map(MerchantResponse::publicFrom)
            .toList();
    }
    
    /**
     * Get all merchants by city and vertical (public view).
     */
    public List<MerchantResponse> getMerchantsByCityAndVertical(String city, Vertical vertical) {
        logger.debug("Retrieving merchants for city: {} and vertical: {}", city, vertical);
        
        return merchantRepository.findByCityAndVertical(city, vertical).stream()
            .filter(Merchant::isPubliclyVisible)
            .map(MerchantResponse::publicFrom)
            .toList();
    }
    
    /**
     * Get all publicly visible merchants.
     */
    public List<MerchantResponse> getAllPublicMerchants() {
        logger.debug("Retrieving all public merchants");
        
        return merchantRepository.findAllPubliclyVisible().stream()
            .map(MerchantResponse::publicFrom)
            .toList();
    }
    
    /**
     * Get all merchants pending approval (admin view).
     */
    public List<MerchantResponse> getPendingApprovalMerchants() {
        logger.debug("Retrieving merchants pending approval");
        
        return merchantRepository.findAllPendingApproval().stream()
            .map(MerchantResponse::from)
            .toList();
    }
    
    /**
     * Get merchants by status (admin view).
     */
    public List<MerchantResponse> getMerchantsByStatus(MerchantStatus status) {
        logger.debug("Retrieving merchants with status: {}", status);
        
        return merchantRepository.findByStatus(status).stream()
            .map(MerchantResponse::from)
            .toList();
    }
    
    /**
     * Get merchants by vertical (admin view).
     */
    public List<MerchantResponse> getMerchantsByVertical(Vertical vertical) {
        logger.debug("Retrieving merchants for vertical: {}", vertical);
        
        return merchantRepository.findByVertical(vertical).stream()
            .map(MerchantResponse::from)
            .toList();
    }
    
    /**
     * Check if business name exists.
     */
    public boolean businessNameExists(String businessName) {
        return merchantRepository.existsByBusinessName(businessName);
    }
    
    /**
     * Check if contact email exists.
     */
    public boolean contactEmailExists(String contactEmail) {
        return merchantRepository.existsByContactEmail(contactEmail);
    }
}