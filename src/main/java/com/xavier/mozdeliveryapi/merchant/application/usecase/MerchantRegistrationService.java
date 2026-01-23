package com.xavier.mozdeliveryapi.merchant.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantRegistrationRequest;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantRegistrationResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.BusinessDetails;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.application.usecase.UseCase;

/**
 * Application service for merchant registration.
 */
@Service
@Transactional
public class MerchantRegistrationService implements UseCase<MerchantRegistrationRequest, MerchantRegistrationResponse> {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantRegistrationService.class);
    
    private final MerchantRepository merchantRepository;
    
    public MerchantRegistrationService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
    
    @Override
    public MerchantRegistrationResponse execute(MerchantRegistrationRequest request) {
        logger.info("Processing merchant registration for business: {}", request.businessName());
        
        // Validate request
        request.validate();
        
        // Check for duplicate business name
        if (merchantRepository.existsByBusinessName(request.businessName())) {
            throw new IllegalArgumentException("Business name already exists: " + request.businessName());
        }
        
        // Check for duplicate contact email
        if (merchantRepository.existsByContactEmail(request.contactEmail())) {
            throw new IllegalArgumentException("Contact email already exists: " + request.contactEmail());
        }
        
        // Create business details
        BusinessDetails businessDetails = new BusinessDetails(
            request.businessName(),
            request.displayName(),
            request.businessRegistrationNumber(),
            request.taxId(),
            request.contactEmail(),
            request.contactPhone(),
            request.businessAddress(),
            request.city(),
            request.country()
        );
        
        // Create merchant
        MerchantId merchantId = MerchantId.generate();
        Merchant merchant = new Merchant(merchantId, businessDetails, request.vertical());
        
        // Save merchant
        Merchant savedMerchant = merchantRepository.save(merchant);
        
        logger.info("Merchant registration completed for ID: {}, Business: {}", 
                   merchantId, request.businessName());
        
        return MerchantRegistrationResponse.success(
            savedMerchant.getMerchantId().toString(),
            savedMerchant.getBusinessName(),
            savedMerchant.getDisplayName(),
            savedMerchant.getBusinessDetails().contactEmail(),
            savedMerchant.getBusinessDetails().city(),
            savedMerchant.getVertical(),
            savedMerchant.getCreatedAt()
        );
    }
}