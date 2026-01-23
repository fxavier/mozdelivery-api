package com.xavier.mozdeliveryapi.payment.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundEligibilityResult;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.PaymentRepository;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.RefundRepository;


/**
 * Implementation of RefundWorkflowService with business rules.
 */
@Service
public class RefundWorkflowServiceImpl implements RefundWorkflowService {
    
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    
    // Business rule constants
    private static final Duration DEFAULT_REFUND_TIME_LIMIT = Duration.ofDays(30);
    private static final Duration CARD_REFUND_TIME_LIMIT = Duration.ofDays(180);
    private static final Duration CASH_ON_DELIVERY_REFUND_TIME_LIMIT = Duration.ofDays(7);
    
    private static final BigDecimal MANUAL_APPROVAL_THRESHOLD = new BigDecimal("1000.00");
    private static final BigDecimal REFUND_FEE_PERCENTAGE = new BigDecimal("0.02"); // 2%
    private static final BigDecimal MAX_REFUND_FEE = new BigDecimal("50.00");
    
    public RefundWorkflowServiceImpl(PaymentRepository paymentRepository,
                                   RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }
    
    @Override
    public RefundEligibilityResult checkRefundEligibility(PaymentId paymentId, Money amount, RefundReason reason) {
        List<String> violations = new ArrayList<>();
        
        // Check if payment exists and is completed
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return RefundEligibilityResult.notEligible("Payment not found");
        }
        
        if (!payment.isCompleted()) {
            violations.add("Payment is not completed");
        }
        
        // Check if payment method supports refunds
        if (!payment.getMethod().supportsRefunds()) {
            violations.add("Payment method does not support refunds");
        }
        
        // Check time limit
        if (!isWithinRefundTimeLimit(paymentId)) {
            violations.add("Refund request is outside the allowed time limit");
        }
        
        // Check refund amount
        Money maxRefundable = calculateMaxRefundableAmount(paymentId);
        if (amount.isGreaterThan(maxRefundable)) {
            violations.add("Refund amount exceeds maximum refundable amount: " + maxRefundable);
        }
        
        // Check for specific reason restrictions
        if (reason == RefundReason.SYSTEM_ERROR && !isEligibleForSystemErrorRefund(payment)) {
            violations.add("Payment is not eligible for system error refund");
        }
        
        if (violations.isEmpty()) {
            return RefundEligibilityResult.success();
        } else {
            return RefundEligibilityResult.notEligible("Refund not eligible", violations);
        }
    }
    
    @Override
    public Money calculateRefundFee(PaymentId paymentId, Money refundAmount) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return Money.zero(refundAmount.currency());
        }
        
        // Different fee structures based on payment method
        return switch (payment.getMethod()) {
            case CASH_ON_DELIVERY -> Money.zero(refundAmount.currency()); // No fee for COD
            case MPESA -> calculateMPesaRefundFee(refundAmount);
            case MULTIBANCO, MB_WAY -> calculateMultibancoRefundFee(refundAmount);
            case CREDIT_CARD, DEBIT_CARD -> calculateCardRefundFee(refundAmount);
        };
    }
    
    @Override
    public Duration getRefundTimeLimit(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return DEFAULT_REFUND_TIME_LIMIT;
        }
        
        return switch (payment.getMethod()) {
            case CREDIT_CARD, DEBIT_CARD -> CARD_REFUND_TIME_LIMIT;
            case CASH_ON_DELIVERY -> CASH_ON_DELIVERY_REFUND_TIME_LIMIT;
            default -> DEFAULT_REFUND_TIME_LIMIT;
        };
    }
    
    @Override
    public boolean isWithinRefundTimeLimit(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return false;
        }
        
        Duration timeLimit = getRefundTimeLimit(paymentId);
        Instant cutoffTime = payment.getCreatedAt().plus(timeLimit);
        
        return Instant.now().isBefore(cutoffTime);
    }
    
    @Override
    public boolean requiresManualApproval(PaymentId paymentId, Money amount, RefundReason reason) {
        // Large amounts require manual approval
        if (amount.amount().compareTo(MANUAL_APPROVAL_THRESHOLD) > 0) {
            return true;
        }
        
        // Certain reasons require manual approval
        if (reason == RefundReason.QUALITY_ISSUE || reason == RefundReason.CUSTOMER_REQUEST) {
            return true;
        }
        
        // Multiple refunds for same payment require approval
        List<Refund> existingRefunds = refundRepository.findByPaymentId(paymentId);
        if (existingRefunds.size() >= 2) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public Duration getEstimatedProcessingTime(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return Duration.ofDays(5); // Default
        }
        
        return switch (payment.getMethod()) {
            case CASH_ON_DELIVERY -> Duration.ofHours(1); // Immediate
            case MPESA -> Duration.ofMinutes(30); // Fast mobile money
            case MULTIBANCO, MB_WAY -> Duration.ofDays(1); // Next business day
            case CREDIT_CARD, DEBIT_CARD -> Duration.ofDays(5); // 3-5 business days
        };
    }
    
    private Money calculateMaxRefundableAmount(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return Money.zero(com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency.USD);
        }
        
        // Calculate total already refunded
        List<Refund> existingRefunds = refundRepository.findByPaymentId(paymentId);
        Money totalRefunded = existingRefunds.stream()
            .filter(refund -> refund.isCompleted() || refund.isInProgress())
            .map(Refund::getAmount)
            .reduce(Money.zero(payment.getCurrency()), Money::add);
        
        return payment.getAmount().subtract(totalRefunded);
    }
    
    private boolean isEligibleForSystemErrorRefund(Payment payment) {
        // Business logic for system error refund eligibility
        // For example, check if payment was made recently, system was down, etc.
        Duration timeSincePayment = Duration.between(payment.getCreatedAt(), Instant.now());
        return timeSincePayment.toDays() <= 7; // System error claims must be made within 7 days
    }
    
    private Money calculateMPesaRefundFee(Money refundAmount) {
        // M-Pesa typically has fixed fees
        BigDecimal fee = new BigDecimal("5.00"); // Fixed fee
        return Money.of(fee, refundAmount.currency());
    }
    
    private Money calculateMultibancoRefundFee(Money refundAmount) {
        // Multibanco may have percentage-based fees
        BigDecimal fee = refundAmount.amount().multiply(REFUND_FEE_PERCENTAGE);
        if (fee.compareTo(MAX_REFUND_FEE) > 0) {
            fee = MAX_REFUND_FEE;
        }
        return Money.of(fee, refundAmount.currency());
    }
    
    private Money calculateCardRefundFee(Money refundAmount) {
        // Card refunds typically don't have fees, but may have processing costs
        return Money.zero(refundAmount.currency());
    }
}
