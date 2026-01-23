package com.xavier.mozdeliveryapi.merchant.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantApprovalRequest;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.application.usecase.UseCase;

/**
 * Application service for merchant approval/rejection.
 */
@Service
@Transactional
public class MerchantApprovalService implements UseCase<MerchantApprovalRequest, MerchantResponse> {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantApprovalService.class);
    
    private final MerchantRepository merchantRepository;
    
    public MerchantApprovalService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
    
    @Override
    public MerchantResponse execute(MerchantApprovalRequest request) {
        logger.info("Processing merchant approval for ID: {}, Approved: {}", 
                   request.merchantId(), request.approved());
        
        // Validate request
        request.validate();
        
        // Find merchant
        MerchantId merchantId = MerchantId.of(request.merchantId());
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + request.merchantId()));
        
        // Process approval or rejection
        if (request.approved()) {
            merchant.approve(request.reviewedBy());
            logger.info("Merchant approved: {} by {}", merchantId, request.reviewedBy());
        } else {
            merchant.reject(request.reason(), request.reviewedBy());
            logger.info("Merchant rejected: {} by {} - Reason: {}", 
                       merchantId, request.reviewedBy(), request.reason());
        }
        
        // Save updated merchant
        Merchant updatedMerchant = merchantRepository.save(merchant);
        
        return MerchantResponse.from(updatedMerchant);
    }
}