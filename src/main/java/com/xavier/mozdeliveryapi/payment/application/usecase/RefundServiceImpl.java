package com.xavier.mozdeliveryapi.payment.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.payment.infra.persistence.PaymentGatewayFactory;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundReason;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundRequest;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundResult;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundStatusResponse;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.PaymentRepository;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.RefundRepository;
import com.xavier.mozdeliveryapi.payment.application.usecase.port.PaymentGateway;

/**
 * Implementation of RefundService.
 */
@Service("paymentRefundService")
@Transactional
public class RefundServiceImpl implements RefundService {
    
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayFactory gatewayFactory;
    
    public RefundServiceImpl(RefundRepository refundRepository,
                           PaymentRepository paymentRepository,
                           PaymentGatewayFactory gatewayFactory) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.gatewayFactory = gatewayFactory;
    }
    
    @Override
    public Refund createRefund(PaymentId paymentId, Money amount, RefundReason reason, String description) {
        // Validate payment exists and can be refunded
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        
        if (!payment.canBeRefunded()) {
            throw new RefundNotAllowedException("Payment cannot be refunded: " + paymentId);
        }
        
        // Validate refund amount
        Money maxRefundable = calculateMaxRefundableAmount(paymentId);
        if (amount.isGreaterThan(maxRefundable)) {
            throw new RefundAmountExceedsLimitException(
                "Refund amount exceeds maximum refundable amount: " + maxRefundable);
        }
        
        // Create refund
        RefundId refundId = RefundId.generate();
        Refund refund = new Refund(refundId, payment.getTenantId(), paymentId, 
                                 amount, reason, description);
        
        return refundRepository.save(refund);
    }
    
    @Override
    public RefundResult processRefund(RefundId refundId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundId));
        
        Payment payment = paymentRepository.findById(refund.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + refund.getPaymentId()));
        
        // Get appropriate gateway
        PaymentGateway gateway = gatewayFactory.getGateway(payment.getMethod())
            .orElseThrow(() -> new PaymentGatewayNotFoundException(
                "No gateway found for payment method: " + payment.getMethod()));
        
        try {
            // Create refund request
            RefundRequest request = RefundRequest.of(
                refundId,
                payment.getPaymentId(),
                refund.getAmount(),
                refund.getReason().name(),
                payment.getGatewayTransactionId()
            );
            
            // Process refund through gateway
            RefundResult result = gateway.processRefund(request);
            
            // Update refund based on result
            if (result.success()) {
                if (result.status() == RefundStatus.PROCESSING) {
                    refund.startProcessing(result.gatewayRefundId());
                } else if (result.status() == RefundStatus.COMPLETED) {
                    refund.startProcessing(result.gatewayRefundId());
                    refund.complete(result.gatewayResponse().toString());
                }
            } else {
                refund.fail(result.errorCode(), result.message());
            }
            
            refundRepository.save(refund);
            return result;
            
        } catch (Exception e) {
            refund.fail("PROCESSING_ERROR", e.getMessage());
            refundRepository.save(refund);
            
            return RefundResult.failure(
                "REFUND_PROCESSING_ERROR",
                "Failed to process refund: " + e.getMessage(),
                java.util.Map.of("error", e.getMessage())
            );
        }
    }
    
    @Override
    public RefundStatusResponse checkRefundStatus(RefundId refundId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundId));
        
        if (refund.getGatewayRefundId() == null) {
            return RefundStatusResponse.of(refund.getStatus(), "Refund not yet processed");
        }
        
        Payment payment = paymentRepository.findById(refund.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + refund.getPaymentId()));
        
        PaymentGateway gateway = gatewayFactory.getGateway(payment.getMethod())
            .orElseThrow(() -> new PaymentGatewayNotFoundException(
                "No gateway found for payment method: " + payment.getMethod()));
        
        try {
            // Check status with gateway (simplified - would need gateway-specific refund status check)
            PaymentStatusResponse paymentStatus = gateway.checkPaymentStatus(refund.getGatewayRefundId());
            
            // Map payment status to refund status
            RefundStatus refundStatus = mapPaymentStatusToRefundStatus(paymentStatus.status());
            
            return RefundStatusResponse.of(refundStatus, paymentStatus.message(), paymentStatus.additionalInfo());
            
        } catch (Exception e) {
            return RefundStatusResponse.of(RefundStatus.FAILED, "Failed to check refund status: " + e.getMessage());
        }
    }
    
    @Override
    public void cancelRefund(RefundId refundId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundId));
        
        if (!refund.canBeCancelled()) {
            throw new RefundCancellationNotAllowedException("Refund cannot be cancelled: " + refundId);
        }
        
        refund.cancel();
        refundRepository.save(refund);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Refund> getRefund(RefundId refundId) {
        return refundRepository.findById(refundId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Refund> getRefundsForPayment(PaymentId paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Refund> getRefundsForTenant(TenantId tenantId) {
        return refundRepository.findByTenantId(tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Money calculateMaxRefundableAmount(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        
        if (!payment.canBeRefunded()) {
            return Money.zero(payment.getCurrency());
        }
        
        // Calculate total already refunded
        List<Refund> existingRefunds = refundRepository.findByPaymentId(paymentId);
        Money totalRefunded = existingRefunds.stream()
            .filter(refund -> refund.isCompleted() || refund.isInProgress())
            .map(Refund::getAmount)
            .reduce(Money.zero(payment.getCurrency()), Money::add);
        
        // Maximum refundable is payment amount minus already refunded
        return payment.getAmount().subtract(totalRefunded);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canRefundPayment(PaymentId paymentId, Money amount) {
        try {
            Money maxRefundable = calculateMaxRefundableAmount(paymentId);
            return !amount.isGreaterThan(maxRefundable);
        } catch (Exception e) {
            return false;
        }
    }
    
    private RefundStatus mapPaymentStatusToRefundStatus(com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> RefundStatus.COMPLETED;
            case PROCESSING -> RefundStatus.PROCESSING;
            case FAILED -> RefundStatus.FAILED;
            case CANCELLED -> RefundStatus.CANCELLED;
            default -> RefundStatus.PENDING;
        };
    }
    
    // Exception classes
    public static class RefundNotFoundException extends RuntimeException {
        public RefundNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class RefundNotAllowedException extends RuntimeException {
        public RefundNotAllowedException(String message) {
            super(message);
        }
    }
    
    public static class RefundAmountExceedsLimitException extends RuntimeException {
        public RefundAmountExceedsLimitException(String message) {
            super(message);
        }
    }
    
    public static class RefundCancellationNotAllowedException extends RuntimeException {
        public RefundCancellationNotAllowedException(String message) {
            super(message);
        }
    }
    
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class PaymentGatewayNotFoundException extends RuntimeException {
        public PaymentGatewayNotFoundException(String message) {
            super(message);
        }
    }
}
