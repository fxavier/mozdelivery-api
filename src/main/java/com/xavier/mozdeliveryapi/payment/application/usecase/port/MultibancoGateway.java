package com.xavier.mozdeliveryapi.payment.application.usecase.port;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MBWayPaymentRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MBWayPaymentResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MultibancoCancelResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MultibancoPaymentStatus;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MultibancoReferenceRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.MultibancoReferenceResult;

/**
 * Interface for Multibanco/MB Way payment gateway integration.
 * Extends the base PaymentGateway with Multibanco specific operations.
 */
public interface MultibancoGateway extends PaymentGateway {
    
    /**
     * Generate Multibanco reference for payment.
     */
    MultibancoReferenceResult generateReference(MultibancoReferenceRequest request);
    
    /**
     * Process MB Way payment.
     */
    MBWayPaymentResult processMBWayPayment(MBWayPaymentRequest request);
    
    /**
     * Check payment status by reference.
     */
    MultibancoPaymentStatus checkPaymentByReference(String reference);
    
    /**
     * Cancel Multibanco reference.
     */
    MultibancoCancelResult cancelReference(String reference);
}