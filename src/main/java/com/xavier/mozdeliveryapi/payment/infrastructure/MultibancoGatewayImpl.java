package com.xavier.mozdeliveryapi.payment.infrastructure;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.MBWayPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.MBWayPaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.MultibancoCancelResult;
import com.xavier.mozdeliveryapi.payment.domain.MultibancoGateway;
import com.xavier.mozdeliveryapi.payment.domain.MultibancoPaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.MultibancoReferenceRequest;
import com.xavier.mozdeliveryapi.payment.domain.MultibancoReferenceResult;
import com.xavier.mozdeliveryapi.payment.domain.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.PaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.domain.RefundRequest;
import com.xavier.mozdeliveryapi.payment.domain.RefundResult;

/**
 * Multibanco/MB Way payment gateway implementation.
 */
@Component
public class MultibancoGatewayImpl implements MultibancoGateway {
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final String entity;
    
    public MultibancoGatewayImpl(RestTemplate restTemplate,
                                @Value("${payment.multibanco.api-url}") String apiUrl,
                                @Value("${payment.multibanco.api-key}") String apiKey,
                                @Value("${payment.multibanco.entity}") String entity) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.entity = entity;
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        try {
            if (request.method() == PaymentMethod.MULTIBANCO) {
                return processMultibancoPayment(request);
            } else if (request.method() == PaymentMethod.MB_WAY) {
                return processMBWayPayment(request);
            } else {
                return PaymentResult.failure(
                    "UNSUPPORTED_METHOD",
                    "Payment method not supported by Multibanco gateway",
                    Map.of()
                );
            }
        } catch (Exception e) {
            return PaymentResult.failure(
                "MULTIBANCO_ERROR",
                "Multibanco payment processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    private PaymentResult processMultibancoPayment(PaymentRequest request) {
        // Generate Multibanco reference
        MultibancoReferenceRequest refRequest = new MultibancoReferenceRequest(
            request.amount(),
            "Payment for order " + request.orderId(),
            LocalDateTime.now().plusDays(7) // 7 days expiry
        );
        
        MultibancoReferenceResult refResult = generateReference(refRequest);
        
        if (refResult.success()) {
            return PaymentResult.processing(
                refResult.reference(),
                "Multibanco reference generated successfully",
                Map.of(
                    "reference", refResult.reference(),
                    "entity", refResult.entity(),
                    "amount", request.amount().amount().toString()
                )
            );
        } else {
            return PaymentResult.failure(
                "REFERENCE_GENERATION_FAILED",
                refResult.message(),
                Map.of()
            );
        }
    }
    
    private PaymentResult processMBWayPayment(PaymentRequest request) {
        String phoneNumber = extractPhoneNumber(request);
        
        MBWayPaymentRequest mbwayRequest = new MBWayPaymentRequest(
            phoneNumber,
            request.amount(),
            "Payment for order " + request.orderId()
        );
        
        MBWayPaymentResult mbwayResult = processMBWayPayment(mbwayRequest);
        
        if (mbwayResult.success()) {
            return PaymentResult.processing(
                mbwayResult.transactionId(),
                "MB Way payment initiated successfully",
                Map.of(
                    "transactionId", mbwayResult.transactionId(),
                    "status", mbwayResult.status()
                )
            );
        } else {
            return PaymentResult.failure(
                "MBWAY_PAYMENT_FAILED",
                mbwayResult.message(),
                Map.of()
            );
        }
    }
    
    @Override
    public RefundResult processRefund(RefundRequest request) {
        try {
            // Multibanco refunds are typically processed manually or through specific APIs
            // This is a simplified implementation
            return RefundResult.processing(
                "REF_" + System.currentTimeMillis(),
                "Refund request submitted for manual processing",
                Map.of(
                    "refundId", request.refundId().toString(),
                    "originalPayment", request.paymentId().toString()
                )
            );
        } catch (Exception e) {
            return RefundResult.failure(
                "MULTIBANCO_REFUND_ERROR",
                "Multibanco refund processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public boolean supportsPaymentMethod(PaymentMethod method) {
        return method == PaymentMethod.MULTIBANCO || method == PaymentMethod.MB_WAY;
    }
    
    @Override
    public boolean supportsCurrency(Currency currency) {
        return currency == Currency.USD; // Multibanco typically uses EUR, but using USD for this example
    }
    
    @Override
    public String getGatewayName() {
        return "Multibanco";
    }
    
    @Override
    public PaymentStatusResponse checkPaymentStatus(String gatewayTransactionId) {
        try {
            MultibancoPaymentStatus status = checkPaymentByReference(gatewayTransactionId);
            
            return PaymentStatusResponse.of(
                status.status(),
                status.message(),
                Map.of("reference", status.reference())
            );
        } catch (Exception e) {
            return PaymentStatusResponse.of(
                PaymentStatus.FAILED,
                "Failed to check payment status: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MultibancoReferenceResult generateReference(MultibancoReferenceRequest request) {
        try {
            // Generate Multibanco reference (simplified implementation)
            // In real implementation, would make API call to Multibanco service
            String reference = generateReferenceNumber();
            
            return new MultibancoReferenceResult(
                true,
                reference,
                entity,
                "Reference generated successfully"
            );
        } catch (Exception e) {
            return new MultibancoReferenceResult(
                false,
                null,
                null,
                "Failed to generate reference: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MBWayPaymentResult processMBWayPayment(MBWayPaymentRequest request) {
        try {
            // Process MB Way payment (simplified implementation)
            // In real implementation, would make API call to MB Way service
            String transactionId = "MBWAY_" + System.currentTimeMillis();
            
            return new MBWayPaymentResult(
                true,
                transactionId,
                "PENDING",
                "MB Way payment initiated successfully"
            );
        } catch (Exception e) {
            return new MBWayPaymentResult(
                false,
                null,
                "FAILED",
                "Failed to process MB Way payment: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MultibancoPaymentStatus checkPaymentByReference(String reference) {
        try {
            // Check payment status by reference (simplified implementation)
            // In real implementation, would make API call to check status
            return new MultibancoPaymentStatus(
                PaymentStatus.COMPLETED,
                reference,
                "Payment completed successfully"
            );
        } catch (Exception e) {
            return new MultibancoPaymentStatus(
                PaymentStatus.FAILED,
                reference,
                "Failed to check payment status: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MultibancoCancelResult cancelReference(String reference) {
        try {
            // Cancel Multibanco reference (simplified implementation)
            // In real implementation, would make API call to cancel reference
            return new MultibancoCancelResult(
                true,
                "Reference cancelled successfully"
            );
        } catch (Exception e) {
            return new MultibancoCancelResult(
                false,
                "Failed to cancel reference: " + e.getMessage()
            );
        }
    }
    
    // Helper methods
    private String generateReferenceNumber() {
        // Generate a 9-digit reference number for Multibanco
        return String.format("%09d", System.currentTimeMillis() % 1000000000L);
    }
    
    private String extractPhoneNumber(PaymentRequest request) {
        // Extract phone number from additional data or customer reference
        return request.additionalData().getOrDefault("phoneNumber", request.customerReference());
    }
}