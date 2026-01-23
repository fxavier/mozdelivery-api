package com.xavier.mozdeliveryapi.merchant.infra.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantApplicationService;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * REST controller for public merchant browsing (no authentication required).
 */
@RestController
@RequestMapping("/api/public/merchants")
@CrossOrigin(origins = "*")
public class PublicMerchantController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicMerchantController.class);
    
    private final MerchantApplicationService merchantService;
    
    public PublicMerchantController(MerchantApplicationService merchantService) {
        this.merchantService = merchantService;
    }
    
    /**
     * Get public merchant information by ID.
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> getPublicMerchant(@PathVariable String merchantId) {
        logger.debug("Getting public merchant info: {}", merchantId);
        
        return merchantService.getPublicMerchant(merchantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all publicly visible merchants.
     */
    @GetMapping
    public ResponseEntity<List<MerchantResponse>> getAllPublicMerchants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Vertical vertical) {
        
        logger.debug("Getting public merchants - City: {}, Vertical: {}", city, vertical);
        
        List<MerchantResponse> merchants;
        
        if (city != null && vertical != null) {
            merchants = merchantService.getMerchantsByCityAndVertical(city, vertical);
        } else if (city != null) {
            merchants = merchantService.getMerchantsByCity(city);
        } else {
            merchants = merchantService.getAllPublicMerchants();
        }
        
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchants by city.
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByCity(@PathVariable String city) {
        logger.debug("Getting merchants for city: {}", city);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByCity(city);
        return ResponseEntity.ok(merchants);
    }
    
    /**
     * Get merchants by city and vertical.
     */
    @GetMapping("/city/{city}/vertical/{vertical}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByCityAndVertical(
            @PathVariable String city,
            @PathVariable Vertical vertical) {
        
        logger.debug("Getting merchants for city: {} and vertical: {}", city, vertical);
        
        List<MerchantResponse> merchants = merchantService.getMerchantsByCityAndVertical(city, vertical);
        return ResponseEntity.ok(merchants);
    }
}