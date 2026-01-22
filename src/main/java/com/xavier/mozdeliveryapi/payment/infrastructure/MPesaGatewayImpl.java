package com.xavier.mozdeliveryapi.payment.infrastructure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.payment.domain.MPesaB2CRequest;
import com.xavier.mozdeliveryapi.payment.domain.MPesaB2CResult;
import com.xavier.mozdeliveryapi.payment.domain.MPesaBalanceResponse;
import com.xavier.mozdeliveryapi.payment.domain.MPesaGateway;
import com.xavier.mozdeliveryapi.payment.domain.MPesaStkPushRequest;
import com.xavier.mozdeliveryapi.payment.domain.MPesaStkPushResult;
import com.xavier.mozdeliveryapi.payment.domain.MPesaTransactionStatus;
import com.xavier.mozdeliveryapi.payment.domain.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.PaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.domain.RefundRequest;
import com.xavier.mozdeliveryapi.payment.domain.RefundResult;

/**
 * M-Pesa payment gateway implementation.
 */
@Component
public class MPesaGatewayImpl implements MPesaGateway {
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String consumerKey;
    private final String consumerSecret;
    private final String shortCode;
    private final String passkey;
    
    public MPesaGatewayImpl(RestTemplate restTemplate,
                           @Value("${payment.mpesa.api-url}") String apiUrl,
                           @Value("${payment.mpesa.consumer-key}") String consumerKey,
                           @Value("${payment.mpesa.consumer-secret}") String consumerSecret,
                           @Value("${payment.mpesa.short-code}") String shortCode,
                           @Value("${payment.mpesa.passkey}") String passkey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.shortCode = shortCode;
        this.passkey = passkey;
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        try {
            // For M-Pesa, we initiate STK Push
            MPesaStkPushRequest stkRequest = new MPesaStkPushRequest(
                extractPhoneNumber(request),
                request.amount(),
                request.orderId().toString(),
                "Payment for order " + request.orderId()
            );
            
            MPesaStkPushResult stkResult = initiateStkPush(stkRequest);
            
            if (stkResult.success()) {
                return PaymentResult.processing(
                    stkResult.checkoutRequestId(),
                    "STK Push initiated successfully",
                    Map.of(
                        "checkoutRequestId", stkResult.checkoutRequestId(),
                        "merchantRequestId", stkResult.merchantRequestId()
                    )
                );
            } else {
                return PaymentResult.failure(
                    stkResult.responseCode(),
                    stkResult.responseDescription(),
                    Map.of("responseCode", stkResult.responseCode())
                );
            }
        } catch (Exception e) {
            return PaymentResult.failure(
                "MPESA_ERROR",
                "M-Pesa payment processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public RefundResult processRefund(RefundRequest request) {
        try {
            // M-Pesa refunds are processed via B2C
            MPesaB2CRequest b2cRequest = new MPesaB2CRequest(
                extractPhoneNumberFromPayment(request.paymentId()),
                request.amount(),
                "BusinessPayment", // Command ID for refunds
                "Refund for " + request.reason()
            );
            
            MPesaB2CResult b2cResult = processB2CPayment(b2cRequest);
            
            if (b2cResult.success()) {
                return RefundResult.processing(
                    b2cResult.conversationId(),
                    "Refund initiated successfully",
                    Map.of(
                        "conversationId", b2cResult.conversationId(),
                        "originatorConversationId", b2cResult.originatorConversationId()
                    )
                );
            } else {
                return RefundResult.failure(
                    b2cResult.responseCode(),
                    b2cResult.responseDescription(),
                    Map.of("responseCode", b2cResult.responseCode())
                );
            }
        } catch (Exception e) {
            return RefundResult.failure(
                "MPESA_REFUND_ERROR",
                "M-Pesa refund processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public boolean supportsPaymentMethod(PaymentMethod method) {
        return method == PaymentMethod.MPESA;
    }
    
    @Override
    public boolean supportsCurrency(Currency currency) {
        return currency == Currency.MZN; // M-Pesa in Mozambique uses MZN
    }
    
    @Override
    public String getGatewayName() {
        return "M-Pesa";
    }
    
    @Override
    public PaymentStatusResponse checkPaymentStatus(String gatewayTransactionId) {
        try {
            MPesaTransactionStatus status = queryStkPushStatus(gatewayTransactionId);
            
            // Map M-Pesa result codes to payment status
            com.xavier.mozdeliveryapi.order.domain.PaymentStatus paymentStatus;
            if ("0".equals(status.resultCode())) {
                paymentStatus = com.xavier.mozdeliveryapi.order.domain.PaymentStatus.COMPLETED;
            } else if ("1032".equals(status.resultCode())) {
                paymentStatus = com.xavier.mozdeliveryapi.order.domain.PaymentStatus.CANCELLED;
            } else {
                paymentStatus = com.xavier.mozdeliveryapi.order.domain.PaymentStatus.FAILED;
            }
            
            return PaymentStatusResponse.of(
                paymentStatus,
                status.resultDesc(),
                Map.of(
                    "resultCode", status.resultCode(),
                    "mpesaReceiptNumber", status.mpesaReceiptNumber() != null ? status.mpesaReceiptNumber() : ""
                )
            );
        } catch (Exception e) {
            return PaymentStatusResponse.of(
                com.xavier.mozdeliveryapi.order.domain.PaymentStatus.FAILED,
                "Failed to check payment status: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MPesaStkPushResult initiateStkPush(MPesaStkPushRequest request) {
        try {
            // Get access token
            String accessToken = getAccessToken();
            
            // Prepare STK Push request
            Map<String, Object> stkPushData = new java.util.HashMap<>();
            stkPushData.put("BusinessShortCode", shortCode);
            stkPushData.put("Password", generatePassword());
            stkPushData.put("Timestamp", generateTimestamp());
            stkPushData.put("TransactionType", "CustomerPayBillOnline");
            stkPushData.put("Amount", request.amount().amount().toString());
            stkPushData.put("PartyA", request.phoneNumber());
            stkPushData.put("PartyB", shortCode);
            stkPushData.put("PhoneNumber", request.phoneNumber());
            stkPushData.put("CallBackURL", getCallbackUrl());
            stkPushData.put("AccountReference", request.accountReference());
            stkPushData.put("TransactionDesc", request.transactionDesc());
            
            // Make API call (simplified - in real implementation would use proper HTTP client)
            // This is a placeholder implementation
            return new MPesaStkPushResult(
                true,
                "ws_CO_" + System.currentTimeMillis(),
                "mr_" + System.currentTimeMillis(),
                "0",
                "Accept the service request successfully."
            );
        } catch (Exception e) {
            return new MPesaStkPushResult(
                false,
                null,
                null,
                "500",
                "Internal server error: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MPesaTransactionStatus queryStkPushStatus(String checkoutRequestId) {
        try {
            // Query STK Push status (simplified implementation)
            // In real implementation, would make API call to M-Pesa
            return new MPesaTransactionStatus(
                "0", // Success
                "The service request is processed successfully.",
                "100.00",
                "NLJ7RT61SV"
            );
        } catch (Exception e) {
            return new MPesaTransactionStatus(
                "1",
                "Failed to query transaction status: " + e.getMessage(),
                null,
                null
            );
        }
    }
    
    @Override
    public MPesaB2CResult processB2CPayment(MPesaB2CRequest request) {
        try {
            // Process B2C payment (simplified implementation)
            // In real implementation, would make API call to M-Pesa
            return new MPesaB2CResult(
                true,
                "AG_" + System.currentTimeMillis(),
                "OC_" + System.currentTimeMillis(),
                "0",
                "Accept the service request successfully."
            );
        } catch (Exception e) {
            return new MPesaB2CResult(
                false,
                null,
                null,
                "500",
                "Internal server error: " + e.getMessage()
            );
        }
    }
    
    @Override
    public MPesaBalanceResponse queryBalance() {
        try {
            // Query account balance (simplified implementation)
            // In real implementation, would make API call to M-Pesa
            return new MPesaBalanceResponse(
                java.math.BigDecimal.valueOf(10000.00),
                java.math.BigDecimal.valueOf(5000.00),
                java.math.BigDecimal.valueOf(100.00)
            );
        } catch (Exception e) {
            return new MPesaBalanceResponse(
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO
            );
        }
    }
    
    // Helper methods
    private String getAccessToken() {
        // Implementation to get OAuth access token from M-Pesa
        return "dummy_access_token";
    }
    
    private String generatePassword() {
        // Generate password for STK Push
        return "dummy_password";
    }
    
    private String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    private String getCallbackUrl() {
        return "https://your-domain.com/api/payments/mpesa/callback";
    }
    
    private String extractPhoneNumber(PaymentRequest request) {
        // Extract phone number from additional data or customer reference
        return request.additionalData().getOrDefault("phoneNumber", request.customerReference());
    }
    
    private String extractPhoneNumberFromPayment(PaymentId paymentId) {
        // In real implementation, would look up payment and extract phone number
        return "254700000000"; // Placeholder
    }
}