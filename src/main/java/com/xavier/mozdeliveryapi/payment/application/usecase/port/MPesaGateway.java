package com.xavier.mozdeliveryapi.payment.application.usecase.port;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaB2CRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaB2CResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaBalanceResponse;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaStkPushRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaStkPushResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MPesaTransactionStatus;

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