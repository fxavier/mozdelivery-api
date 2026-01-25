package com.xavier.mozdeliveryapi.deliveryconfirmation.infra.web;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CompleteDeliveryRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DCCStatusResponse;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DeliveryCompletionResult;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DeliveryConfirmationApplicationService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

import jakarta.validation.Valid;

/**
 * REST controller for delivery confirmation operations.
 * Provides APIs for courier app to complete deliveries using DCC validation.
 */
@RestController
@RequestMapping("/api/v1/delivery-confirmation")
public class DeliveryConfirmationController {
    
    private final DeliveryConfirmationApplicationService deliveryConfirmationService;
    
    public DeliveryConfirmationController(DeliveryConfirmationApplicationService deliveryConfirmationService) {
        this.deliveryConfirmationService = Objects.requireNonNull(deliveryConfirmationService,
            "Delivery confirmation service cannot be null");
    }
    
    /**
     * Complete a delivery using confirmation code.
     * This is the primary endpoint for courier apps to complete deliveries.
     * 
     * @param request The delivery completion request
     * @return The result of the completion attempt
     */
    @PostMapping("/complete")
    @PreAuthorize("hasRole('COURIER') or hasRole('ADMIN')")
    public ResponseEntity<DeliveryCompletionResult> completeDelivery(@Valid @RequestBody CompleteDeliveryRequest request) {
        DeliveryCompletionResult result = deliveryConfirmationService.completeDelivery(request);
        
        if (result.successful()) {
            return ResponseEntity.ok(result);
        } else {
            // Return 400 for validation failures, but still include the result for client handling
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Get the status of a delivery confirmation code.
     * Useful for courier apps to check code validity before attempting completion.
     * 
     * @param orderId The order ID
     * @return The DCC status information
     */
    @GetMapping("/status/{orderId}")
    @PreAuthorize("hasRole('COURIER') or hasRole('ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<DCCStatusResponse> getCodeStatus(@PathVariable String orderId) {
        try {
            OrderId orderIdObj = OrderId.of(orderId);
            DCCStatusResponse status = deliveryConfirmationService.getCodeStatus(orderIdObj);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Resend a delivery confirmation code.
     * Generates a new code and sends it to the customer.
     * 
     * @param orderId The order ID
     * @return Success response
     */
    @PostMapping("/resend/{orderId}")
    @PreAuthorize("hasRole('COURIER') or hasRole('ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<Void> resendDeliveryCode(@PathVariable String orderId) {
        try {
            OrderId orderIdObj = OrderId.of(orderId);
            deliveryConfirmationService.resendDeliveryCode(orderIdObj);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}