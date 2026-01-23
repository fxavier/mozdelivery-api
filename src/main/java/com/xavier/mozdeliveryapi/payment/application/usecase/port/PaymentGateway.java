package com.xavier.mozdeliveryapi.payment.application.usecase.port;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentStatusResponse;


/**
 * Interface for payment gateway implementations.
 */
public interface PaymentGateway {
    
    /**
     * Process a payment through this gateway.
     */
    PaymentResult processPayment(PaymentRequest request);
    
    /**
     * Process a refund through this gateway.
     */
    RefundResult processRefund(RefundRequest request);
    
    /**
     * Check if this gateway supports the given payment method.
     */
    boolean supportsPaymentMethod(PaymentMethod method);
    
    /**
     * Check if this gateway supports the given currency.
     */
    boolean supportsCurrency(Currency currency);
    
    /**
     * Get the gateway name/identifier.
     */
    String getGatewayName();
    
    /**
     * Check payment status with the gateway.
     */
    PaymentStatusResponse checkPaymentStatus(String gatewayTransactionId);
}
