package com.xavier.mozdeliveryapi.payment.domain;

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