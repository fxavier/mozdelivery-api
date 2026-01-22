package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;

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