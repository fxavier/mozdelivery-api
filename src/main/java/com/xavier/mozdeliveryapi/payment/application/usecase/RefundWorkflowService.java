package com.xavier.mozdeliveryapi.payment.application.usecase;


import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;

import java.time.Duration;
import java.time.Instant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundEligibilityResult;

/**
 * Domain service for refund workflow and business rules.
 */
public interface RefundWorkflowService {
    
    /**
     * Check if a refund is allowed based on business rules.
     */
    RefundEligibilityResult checkRefundEligibility(PaymentId paymentId, Money amount, RefundReason reason);
    
    /**
     * Calculate refund processing fee if applicable.
     */
    Money calculateRefundFee(PaymentId paymentId, Money refundAmount);
    
    /**
     * Get refund time limit for a payment.
     */
    Duration getRefundTimeLimit(PaymentId paymentId);
    
    /**
     * Check if refund is within time limit.
     */
    boolean isWithinRefundTimeLimit(PaymentId paymentId);
    
    /**
     * Determine if refund requires manual approval.
     */
    boolean requiresManualApproval(PaymentId paymentId, Money amount, RefundReason reason);
    
    /**
     * Get estimated refund processing time.
     */
    Duration getEstimatedProcessingTime(PaymentId paymentId);
}