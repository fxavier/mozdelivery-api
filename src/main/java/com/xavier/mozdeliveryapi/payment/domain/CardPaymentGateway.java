package com.xavier.mozdeliveryapi.payment.domain;

/**
 * Interface for card payment gateway integration.
 * Extends the base PaymentGateway with card-specific operations.
 */
public interface CardPaymentGateway extends PaymentGateway {
    
    /**
     * Process card payment with card details.
     */
    CardPaymentResult processCardPayment(CardPaymentRequest request);
    
    /**
     * Process payment with saved card token.
     */
    CardPaymentResult processTokenPayment(CardTokenPaymentRequest request);
    
    /**
     * Tokenize card for future payments.
     */
    CardTokenizationResult tokenizeCard(CardTokenizationRequest request);
    
    /**
     * Verify card details without charging.
     */
    CardVerificationResult verifyCard(CardVerificationRequest request);
    
    /**
     * Process 3D Secure authentication.
     */
    ThreeDSecureResult process3DSecure(ThreeDSecureRequest request);
}