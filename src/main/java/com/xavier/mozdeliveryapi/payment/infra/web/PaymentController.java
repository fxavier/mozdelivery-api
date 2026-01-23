package com.xavier.mozdeliveryapi.payment.infra.web;

import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;
import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.payment.application.dto.CreatePaymentRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.CreateRefundRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundStatusResponse;
import com.xavier.mozdeliveryapi.payment.application.usecase.PaymentApplicationService;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

/**
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment processing and management operations")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentApplicationService paymentApplicationService;
    
    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }
    
    @Operation(summary = "Create payment", description = "Creates a new payment for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        
        logger.info("Creating payment for order: {} in tenant: {}", 
            request.orderId(), TenantContext.getCurrentTenant());
        
        try {
            PaymentResponse response = paymentApplicationService.createPayment(request);
            logger.info("Payment created successfully with ID: {}", response.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating payment for order: {}", request.orderId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Process payment", description = "Processes a payment through the appropriate gateway")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Payment processing failed"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/{paymentId}/process")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<PaymentResponse> processPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Processing payment: {}", paymentId);
        
        try {
            PaymentResponse response = paymentApplicationService.processPayment(PaymentId.of(paymentId));
            logger.info("Payment processed successfully: {}", paymentId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get payment", description = "Retrieves a payment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Getting payment: {}", paymentId);
        
        try {
            PaymentResponse response = paymentApplicationService.getPayment(PaymentId.of(paymentId));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get payments for order", description = "Retrieves all payments for a specific order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsForOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        
        logger.info("Getting payments for order: {}", orderId);
        
        try {
            List<PaymentResponse> payments = paymentApplicationService
                .getPaymentsForOrder(OrderId.of(orderId));
            
            return ResponseEntity.ok(payments);
            
        } catch (Exception e) {
            logger.error("Error getting payments for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get payments for tenant", description = "Retrieves all payments for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsForTenant() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting payments for tenant: {}", tenantId);
        
        try {
            List<PaymentResponse> payments = paymentApplicationService
                .getPaymentsForTenant(TenantId.of(tenantId));
            
            return ResponseEntity.ok(payments);
            
        } catch (Exception e) {
            logger.error("Error getting payments for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Cancel payment", description = "Cancels a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Payment cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<Void> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Cancelling payment: {}", paymentId);
        
        try {
            paymentApplicationService.cancelPayment(PaymentId.of(paymentId));
            logger.info("Payment cancelled successfully: {}", paymentId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error cancelling payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Check payment status", description = "Checks the current status of a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{paymentId}/status")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<PaymentStatusResponse> checkPaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Checking payment status: {}", paymentId);
        
        try {
            PaymentStatusResponse response = paymentApplicationService
                .checkPaymentStatus(PaymentId.of(paymentId));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking payment status: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Create refund", description = "Creates a refund for a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refund created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refund request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/refunds")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<RefundResponse> createRefund(
            @Valid @RequestBody CreateRefundRequest request) {
        
        logger.info("Creating refund for payment: {}", request.paymentId());
        
        try {
            RefundResponse response = paymentApplicationService.createRefund(request);
            logger.info("Refund created successfully with ID: {}", response.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating refund for payment: {}", request.paymentId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Process refund", description = "Processes a refund through the appropriate gateway")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Refund processing failed"),
        @ApiResponse(responseCode = "404", description = "Refund not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/refunds/{refundId}/process")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<RefundResponse> processRefund(
            @Parameter(description = "Refund ID") @PathVariable String refundId) {
        
        logger.info("Processing refund: {}", refundId);
        
        try {
            RefundResponse response = paymentApplicationService.processRefund(RefundId.of(refundId));
            logger.info("Refund processed successfully: {}", refundId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing refund: {}", refundId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get refund", description = "Retrieves a refund by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund found"),
        @ApiResponse(responseCode = "404", description = "Refund not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/refunds/{refundId}")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<RefundResponse> getRefund(
            @Parameter(description = "Refund ID") @PathVariable String refundId) {
        
        logger.info("Getting refund: {}", refundId);
        
        try {
            RefundResponse response = paymentApplicationService.getRefund(RefundId.of(refundId));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting refund: {}", refundId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get refunds for payment", description = "Retrieves all refunds for a specific payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{paymentId}/refunds")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<List<RefundResponse>> getRefundsForPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Getting refunds for payment: {}", paymentId);
        
        try {
            List<RefundResponse> refunds = paymentApplicationService
                .getRefundsForPayment(PaymentId.of(paymentId));
            
            return ResponseEntity.ok(refunds);
            
        } catch (Exception e) {
            logger.error("Error getting refunds for payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get refunds for tenant", description = "Retrieves all refunds for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/refunds")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<List<RefundResponse>> getRefundsForTenant() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting refunds for tenant: {}", tenantId);
        
        try {
            List<RefundResponse> refunds = paymentApplicationService
                .getRefundsForTenant(TenantId.of(tenantId));
            
            return ResponseEntity.ok(refunds);
            
        } catch (Exception e) {
            logger.error("Error getting refunds for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Cancel refund", description = "Cancels a refund")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Refund cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Refund not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/refunds/{refundId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<Void> cancelRefund(
            @Parameter(description = "Refund ID") @PathVariable String refundId) {
        
        logger.info("Cancelling refund: {}", refundId);
        
        try {
            paymentApplicationService.cancelRefund(RefundId.of(refundId));
            logger.info("Refund cancelled successfully: {}", refundId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error cancelling refund: {}", refundId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Check refund status", description = "Checks the current status of a refund")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/refunds/{refundId}/status")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<RefundStatusResponse> checkRefundStatus(
            @Parameter(description = "Refund ID") @PathVariable String refundId) {
        
        logger.info("Checking refund status: {}", refundId);
        
        try {
            RefundStatusResponse response = paymentApplicationService
                .checkRefundStatus(RefundId.of(refundId));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking refund status: {}", refundId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Calculate max refundable amount", description = "Calculates the maximum refundable amount for a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Max refundable amount calculated successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{paymentId}/max-refundable")
    @PreAuthorize("hasAuthority('SCOPE_payment:read')")
    public ResponseEntity<Money> calculateMaxRefundableAmount(
            @Parameter(description = "Payment ID") @PathVariable String paymentId) {
        
        logger.info("Calculating max refundable amount for payment: {}", paymentId);
        
        try {
            Money maxRefundable = paymentApplicationService
                .calculateMaxRefundableAmount(PaymentId.of(paymentId));
            
            return ResponseEntity.ok(maxRefundable);
            
        } catch (Exception e) {
            logger.error("Error calculating max refundable amount for payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
