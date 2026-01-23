package com.xavier.mozdeliveryapi.merchant.infra.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantRegistrationRequest;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantRegistrationResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantRegistrationService;

/**
 * REST controller for merchant registration.
 */
@RestController
@RequestMapping("/api/v1/merchants")
@CrossOrigin(origins = "*")
public class MerchantRegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantRegistrationController.class);
    
    private final MerchantRegistrationService registrationService;
    
    public MerchantRegistrationController(MerchantRegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    /**
     * Register a new merchant.
     */
    @PostMapping("/register")
    public ResponseEntity<MerchantRegistrationResponse> registerMerchant(
            @RequestBody MerchantRegistrationRequest request) {
        
        logger.info("Received merchant registration request for business: {}", request.businessName());
        
        try {
            MerchantRegistrationResponse response = registrationService.execute(request);
            logger.info("Merchant registration successful for ID: {}", response.merchantId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Merchant registration validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new MerchantRegistrationResponse(
                    null, null, null, null, null, null, null, 
                    "Registration failed: " + e.getMessage(), null
                )
            );
            
        } catch (Exception e) {
            logger.error("Merchant registration failed unexpectedly", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new MerchantRegistrationResponse(
                    null, null, null, null, null, null, null,
                    "Registration failed due to system error. Please try again later.", null
                )
            );
        }
    }
}