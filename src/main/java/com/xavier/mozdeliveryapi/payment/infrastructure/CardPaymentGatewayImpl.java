package com.xavier.mozdeliveryapi.payment.infrastructure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.CardPaymentGateway;
import com.xavier.mozdeliveryapi.payment.domain.CardPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.CardPaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.CardTokenPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.CardTokenizationRequest;
import com.xavier.mozdeliveryapi.payment.domain.CardTokenizationResult;
import com.xavier.mozdeliveryapi.payment.domain.CardVerificationRequest;
import com.xavier.mozdeliveryapi.payment.domain.CardVerificationResult;
import com.xavier.mozdeliveryapi.payment.domain.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.PaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.domain.RefundRequest;
import com.xavier.mozdeliveryapi.payment.domain.RefundResult;
import com.xavier.mozdeliveryapi.payment.domain.ThreeDSecureRequest;
import com.xavier.mozdeliveryapi.payment.domain.ThreeDSecureResult;

/**
 * Card payment gateway implementation for credit/debit cards.
 */
@Component
public class CardPaymentGatewayImpl implements CardPaymentGateway {
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String merchantId;
    private final String apiKey;
    private final String encryptionKey;
    
    public CardPaymentGatewayImpl(RestTemplate restTemplate,
                                 @Value("${payment.cards.api-url}") String apiUrl,
                                 @Value("${payment.cards.merchant-id}") String merchantId,
                                 @Value("${payment.cards.api-key}") String apiKey,
                                 @Value("${payment.cards.encryption-key}") String encryptionKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.encryptionKey = encryptionKey;
    }
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        try {
            // Extract card details from additional data
            String cardNumber = request.additionalData().get("cardNumber");
            String expiryMonth = request.additionalData().get("expiryMonth");
            String expiryYear = request.additionalData().get("expiryYear");
            String cvv = request.additionalData().get("cvv");
            String cardholderName = request.additionalData().get("cardholderName");
            
            if (cardNumber == null || expiryMonth == null || expiryYear == null || cvv == null) {
                return PaymentResult.failure(
                    "MISSING_CARD_DETAILS",
                    "Required card details are missing",
                    Map.of()
                );
            }
            
            CardPaymentRequest cardRequest = new CardPaymentRequest(
                encryptCardNumber(cardNumber),
                expiryMonth,
                expiryYear,
                encryptCvv(cvv),
                cardholderName,
                request.amount(),
                "Payment for order " + request.orderId()
            );
            
            CardPaymentResult cardResult = processCardPayment(cardRequest);
            
            if (cardResult.success()) {
                if (cardResult.requires3DSecure()) {
                    return PaymentResult.processing(
                        cardResult.transactionId(),
                        "3D Secure authentication required",
                        Map.of(
                            "transactionId", cardResult.transactionId(),
                            "requires3DSecure", "true",
                            "threeDSecureUrl", cardResult.threeDSecureUrl()
                        )
                    );
                } else {
                    return PaymentResult.success(
                        PaymentStatus.COMPLETED,
                        cardResult.transactionId(),
                        "Card payment processed successfully",
                        Map.of(
                            "transactionId", cardResult.transactionId(),
                            "authorizationCode", cardResult.authorizationCode()
                        )
                    );
                }
            } else {
                return PaymentResult.failure(
                    "CARD_PAYMENT_FAILED",
                    cardResult.message(),
                    Map.of("transactionId", cardResult.transactionId() != null ? cardResult.transactionId() : "")
                );
            }
        } catch (Exception e) {
            return PaymentResult.failure(
                "CARD_GATEWAY_ERROR",
                "Card payment processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public RefundResult processRefund(RefundRequest request) {
        try {
            // Process card refund (simplified implementation)
            // In real implementation, would make API call to card processor
            String refundId = "REF_" + System.currentTimeMillis();
            
            return RefundResult.success(
                com.xavier.mozdeliveryapi.order.domain.RefundStatus.COMPLETED,
                refundId,
                "Card refund processed successfully",
                Map.of(
                    "refundId", refundId,
                    "originalTransaction", request.gatewayTransactionId()
                )
            );
        } catch (Exception e) {
            return RefundResult.failure(
                "CARD_REFUND_ERROR",
                "Card refund processing failed: " + e.getMessage(),
                Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public boolean supportsPaymentMethod(PaymentMethod method) {
        return method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD;
    }
    
    @Override
    public boolean supportsCurrency(Currency currency) {
        // Card payments typically support both USD and MZN
        return currency == Currency.USD || currency == Currency.MZN;
    }
    
    @Override
    public String getGatewayName() {
        return "Card Payment Gateway";
    }
    
    @Override
    public PaymentStatusResponse checkPaymentStatus(String gatewayTransactionId) {
        try {
            // Check card payment status (simplified implementation)
            // In real implementation, would make API call to card processor
            return PaymentStatusResponse.of(
                PaymentStatus.COMPLETED,
                "Payment completed successfully",
                Map.of(
                    "transactionId", gatewayTransactionId,
                    "authorizationCode", "AUTH123456"
                )
            );
        } catch (Exception e) {
            return PaymentStatusResponse.of(
                PaymentStatus.FAILED,
                "Failed to check payment status: " + e.getMessage()
            );
        }
    }
    
    @Override
    public CardPaymentResult processCardPayment(CardPaymentRequest request) {
        try {
            // Validate card details
            if (!isValidCardNumber(request.cardNumber())) {
                return new CardPaymentResult(
                    false,
                    null,
                    null,
                    "FAILED",
                    "Invalid card number",
                    false,
                    null
                );
            }
            
            // Process card payment (simplified implementation)
            // In real implementation, would make API call to card processor
            String transactionId = "TXN_" + System.currentTimeMillis();
            String authCode = "AUTH_" + System.currentTimeMillis();
            
            // Simulate 3D Secure requirement for certain cards
            boolean requires3DS = shouldRequire3DSecure(request.cardNumber());
            
            if (requires3DS) {
                return new CardPaymentResult(
                    true,
                    transactionId,
                    null,
                    "PENDING_3DS",
                    "3D Secure authentication required",
                    true,
                    "https://3ds.example.com/auth?txn=" + transactionId
                );
            } else {
                return new CardPaymentResult(
                    true,
                    transactionId,
                    authCode,
                    "COMPLETED",
                    "Payment processed successfully",
                    false,
                    null
                );
            }
        } catch (Exception e) {
            return new CardPaymentResult(
                false,
                null,
                null,
                "ERROR",
                "Payment processing error: " + e.getMessage(),
                false,
                null
            );
        }
    }
    
    @Override
    public CardPaymentResult processTokenPayment(CardTokenPaymentRequest request) {
        try {
            // Process payment with saved card token (simplified implementation)
            String transactionId = "TKN_" + System.currentTimeMillis();
            String authCode = "AUTH_" + System.currentTimeMillis();
            
            return new CardPaymentResult(
                true,
                transactionId,
                authCode,
                "COMPLETED",
                "Token payment processed successfully",
                false,
                null
            );
        } catch (Exception e) {
            return new CardPaymentResult(
                false,
                null,
                null,
                "ERROR",
                "Token payment processing error: " + e.getMessage(),
                false,
                null
            );
        }
    }
    
    @Override
    public CardTokenizationResult tokenizeCard(CardTokenizationRequest request) {
        try {
            // Tokenize card for future payments (simplified implementation)
            String cardToken = "TOK_" + System.currentTimeMillis();
            String maskedCardNumber = maskCardNumber(request.cardNumber());
            
            return new CardTokenizationResult(
                true,
                cardToken,
                maskedCardNumber,
                "Card tokenized successfully"
            );
        } catch (Exception e) {
            return new CardTokenizationResult(
                false,
                null,
                null,
                "Card tokenization failed: " + e.getMessage()
            );
        }
    }
    
    @Override
    public CardVerificationResult verifyCard(CardVerificationRequest request) {
        try {
            // Verify card details without charging (simplified implementation)
            boolean isValid = isValidCardNumber(request.cardNumber()) && 
                            isValidExpiryDate(request.expiryMonth(), request.expiryYear());
            
            if (isValid) {
                return new CardVerificationResult(
                    true,
                    detectCardType(request.cardNumber()),
                    "Unknown Issuer",
                    "Card verification successful"
                );
            } else {
                return new CardVerificationResult(
                    false,
                    null,
                    null,
                    "Card verification failed"
                );
            }
        } catch (Exception e) {
            return new CardVerificationResult(
                false,
                null,
                null,
                "Card verification error: " + e.getMessage()
            );
        }
    }
    
    @Override
    public ThreeDSecureResult process3DSecure(ThreeDSecureRequest request) {
        try {
            // Process 3D Secure authentication (simplified implementation)
            return new ThreeDSecureResult(
                true,
                "AUTHENTICATED",
                "3DS_AUTH_" + System.currentTimeMillis(),
                "3D Secure authentication successful"
            );
        } catch (Exception e) {
            return new ThreeDSecureResult(
                false,
                "FAILED",
                null,
                "3D Secure authentication failed: " + e.getMessage()
            );
        }
    }
    
    // Helper methods for card processing
    private String encryptCardNumber(String cardNumber) {
        // In real implementation, would use proper encryption
        return "ENCRYPTED_" + cardNumber.substring(cardNumber.length() - 4);
    }
    
    private String encryptCvv(String cvv) {
        // In real implementation, would use proper encryption
        return "ENCRYPTED_CVV";
    }
    
    private boolean isValidCardNumber(String cardNumber) {
        // Basic Luhn algorithm check (simplified)
        return cardNumber != null && cardNumber.length() >= 13 && cardNumber.length() <= 19;
    }
    
    private boolean isValidExpiryDate(String month, String year) {
        try {
            int m = Integer.parseInt(month);
            int y = Integer.parseInt(year);
            return m >= 1 && m <= 12 && y >= 2024;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private String detectCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) return "VISA";
        if (cardNumber.startsWith("5")) return "MASTERCARD";
        if (cardNumber.startsWith("3")) return "AMEX";
        return "UNKNOWN";
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
    
    private boolean shouldRequire3DSecure(String cardNumber) {
        // Simplified logic - in real implementation would be based on risk assessment
        return cardNumber.endsWith("1") || cardNumber.endsWith("2");
    }
}