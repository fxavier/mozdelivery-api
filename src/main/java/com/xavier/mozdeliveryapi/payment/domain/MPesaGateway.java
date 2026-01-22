package com.xavier.mozdeliveryapi.payment.domain;

/**
 * Interface for M-Pesa payment gateway integration.
 * Extends the base PaymentGateway with M-Pesa specific operations.
 */
public interface MPesaGateway extends PaymentGateway {
    
    /**
     * Initiate STK Push for customer payment.
     */
    MPesaStkPushResult initiateStkPush(MPesaStkPushRequest request);
    
    /**
     * Query STK Push transaction status.
     */
    MPesaTransactionStatus queryStkPushStatus(String checkoutRequestId);
    
    /**
     * Process B2C payment (business to customer).
     */
    MPesaB2CResult processB2CPayment(MPesaB2CRequest request);
    
    /**
     * Query account balance.
     */
    MPesaBalanceResponse queryBalance();
}