package com.xavier.mozdeliveryapi.payment.application.usecase.port;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardPaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardTokenPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardTokenizationRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardTokenizationResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardVerificationRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.CardVerificationResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.ThreeDSecureRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.ThreeDSecureResult;

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