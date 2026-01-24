package com.xavier.mozdeliveryapi.order.infra.web;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderRequest;
import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderResponse;
import com.xavier.mozdeliveryapi.order.application.dto.GuestTrackingResponse;
import com.xavier.mozdeliveryapi.order.application.usecase.GuestCheckoutApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for guest checkout operations.
 * Provides public APIs for guest order creation and tracking.
 */
@RestController
@RequestMapping("/api/public/orders/guest")
@Tag(name = "Guest Checkout", description = "Guest order operations without registration")
@CrossOrigin(origins = "*")
public class GuestCheckoutController {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestCheckoutController.class);
    
    private final GuestCheckoutApplicationService guestCheckoutApplicationService;
    
    public GuestCheckoutController(GuestCheckoutApplicationService guestCheckoutApplicationService) {
        this.guestCheckoutApplicationService = Objects.requireNonNull(
            guestCheckoutApplicationService, "Guest checkout application service cannot be null");
    }
    
    @PostMapping
    @Operation(
        summary = "Create guest order",
        description = "Create an order without user registration using contact information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Tenant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GuestOrderResponse> createGuestOrder(
            @Valid @RequestBody GuestOrderRequest request) {
        
        logger.info("Creating guest order for tenant: {}", request.tenantId());
        
        try {
            GuestOrderResponse response = guestCheckoutApplicationService.createGuestOrder(request);
            
            logger.info("Guest order created successfully: {}", response.orderId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid guest order request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating guest order", e);
            throw e;
        }
    }
    
    @GetMapping("/track")
    @Operation(
        summary = "Track guest order",
        description = "Track order status using guest tracking token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order tracking information retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid tracking token"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GuestTrackingResponse> trackGuestOrder(
            @Parameter(description = "Guest tracking token", required = true)
            @RequestParam("token") String trackingToken) {
        
        logger.debug("Tracking guest order with token: {}", trackingToken);
        
        try {
            GuestTrackingResponse response = guestCheckoutApplicationService.trackGuestOrder(trackingToken);
            
            logger.debug("Guest order tracking retrieved: {}", response.orderId());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid tracking token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error tracking guest order", e);
            throw e;
        }
    }
    
    @PostMapping("/resend-code")
    @Operation(
        summary = "Resend delivery confirmation code",
        description = "Resend delivery confirmation code for guest order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery code resent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid tracking token"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resendDeliveryCode(
            @Parameter(description = "Guest tracking token", required = true)
            @RequestParam("token") String trackingToken) {
        
        logger.info("Resending delivery code for token: {}", trackingToken);
        
        try {
            guestCheckoutApplicationService.resendDeliveryCode(trackingToken);
            
            logger.info("Delivery code resent successfully");
            
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid tracking token for resend: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error resending delivery code", e);
            throw e;
        }
    }
    
    @PostMapping("/convert-to-customer")
    @Operation(
        summary = "Convert guest to registered customer",
        description = "Convert guest order to registered customer order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guest converted to customer successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> convertGuestToCustomer(
            @Parameter(description = "Guest tracking token", required = true)
            @RequestParam("token") String trackingToken,
            @Parameter(description = "Customer ID", required = true)
            @RequestParam("customerId") String customerId) {
        
        logger.info("Converting guest to customer: {}", customerId);
        
        try {
            guestCheckoutApplicationService.convertGuestToCustomer(trackingToken, customerId);
            
            logger.info("Guest converted to customer successfully");
            
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid conversion request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error converting guest to customer", e);
            throw e;
        }
    }
}