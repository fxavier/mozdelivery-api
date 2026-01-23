package com.xavier.mozdeliveryapi.merchant.infra.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantApprovalRequest;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantApplicationService;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantApprovalService;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * REST controller for merchant management operations.
 */
@RestController
@RequestMapping("/api/v1/merchants")
@CrossOrigin(origins = "*")
public class MerchantManagementController {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantManagementController.class);
    
    private final MerchantApplicationService merchantService;
    private final MerchantApprovalService approvalService;
    
    public MerchantManagementController(
            MerchantApplicationService merchantService,
            MerchantApprovalService approvalService) {
        this.merchantService = merchantService;
        this.approvalService = approvalService;
    }
    
    /**
     * Get merchant by ID (admin view).
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> getMerchant(@PathVariable String merchantId) {
        logger.debug("Getting merchant: {}", merchantId);
        
        return merchantService.getMerchant(merchantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all merchants pending approval.
     */
    @GetMapping("/pending-approval")
    public ResponseEntity<List<MerchantResponse>> getPendingApprovalMerchants() {
        logger.debug("Getting merchants pending approval");
        
        List<MerchantResponse> merchants = merchantService.getPendingApprovalMerchants();
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchants by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByStatus(@PathVariable MerchantStatus status) {
        logger.debug("Getting merchants with status: {}", status);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByStatus(status);
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchants by vertical.
     */
    @GetMapping("/vertical/{vertical}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByVertical(@PathVariable Vertical vertical) {
        logger.debug("Getting merchants for vertical: {}", vertical);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByVertical(vertical);
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Approve or reject a merchant.
     */
    @PostMapping("/{merchantId}/approval")
    public ResponseEntity<MerchantResponse> processMerchantApproval(
            @PathVariable String merchantId,
            @RequestBody MerchantApprovalRequest request) {
        
        logger.info("Processing approval for merchant: {}, Approved: {}", merchantId, request.approved());
        
        try {
            // Ensure merchantId matches
            MerchantApprovalRequest updatedRequest = new MerchantApprovalRequest(
                merchantId, request.approved(), request.reason(), request.reviewedBy()
            );
            
            MerchantResponse response = approvalService.execute(updatedRequest);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Merchant approval validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Merchant approval failed unexpectedly", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Check if business name exists.
     */
    @GetMapping("/check/business-name")
    public ResponseEntity<Boolean> checkBusinessNameExists(@RequestParam String businessName) {
        boolean exists = merchantService.businessNameExists(businessName);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Check if contact email exists.
     */
    @GetMapping("/check/contact-email")
    public ResponseEntity<Boolean> checkContactEmailExists(@RequestParam String contactEmail) {
        boolean exists = merchantService.contactEmailExists(contactEmail);
        return ResponseEntity.ok(exists);
    }
}