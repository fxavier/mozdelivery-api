package com.xavier.mozdeliveryapi.payment.application.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentRequest;
import com.xavier.mozdeliveryapi.payment.application.usecase.PaymentService;
import com.xavier.mozdeliveryapi.payment.domain.entity.Refund;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;
import com.xavier.mozdeliveryapi.payment.application.usecase.RefundService;
import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.payment.application.dto.CreatePaymentRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.CreateRefundRequest;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.PaymentStatusResponse;
import com.xavier.mozdeliveryapi.payment.application.dto.RefundStatusResponse;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;

/**
 * Application service for payment operations.
 */
@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {

    private final PaymentService paymentService;
    private final RefundService refundService;

    public PaymentApplicationServiceImpl(PaymentService paymentService, RefundService refundService) {
        this.paymentService = Objects.requireNonNull(paymentService, "Payment service cannot be null");
        this.refundService = Objects.requireNonNull(refundService, "Refund service cannot be null");
    }

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Objects.requireNonNull(request, "Create payment request cannot be null");

        String tenantValue = TenantContext.getCurrentTenant();
        if (tenantValue == null) {
            throw new IllegalStateException("Tenant context is required to create a payment");
        }
        TenantId tenantId = TenantId.of(tenantValue);

        if (!request.amount().currency().equals(request.currency())) {
            throw new IllegalArgumentException("Amount currency does not match request currency");
        }

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(PaymentId.generate())
                .tenantId(tenantId)
                .orderId(request.orderId())
                .method(request.paymentMethod())
                .amount(request.amount())
                .customerReference(request.description())
                .additionalData(Map.of())
                .build();

        Payment payment = paymentService.createPayment(paymentRequest);
        return PaymentResponse.from(payment);
    }

    @Override
    public PaymentResponse processPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");

        paymentService.processPayment(paymentId);
        Payment payment = paymentService.getPayment(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        return PaymentResponse.from(payment);
    }

    @Override
    public PaymentResponse getPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");

        Payment payment = paymentService.getPayment(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        return PaymentResponse.from(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsForOrder(com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        return paymentService.getPaymentsForOrder(orderId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return paymentService.getPaymentsForTenant(tenantId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Override
    public void cancelPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        paymentService.cancelPayment(paymentId);
    }

    @Override
    public PaymentStatusResponse checkPaymentStatus(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");

        com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentStatusResponse status =
                paymentService.checkPaymentStatus(paymentId);

        return new PaymentStatusResponse(
                paymentId.value().toString(),
                status.status(),
                status.status().name(),
                status.message(),
                Instant.now()
        );
    }

    @Override
    public RefundResponse createRefund(CreateRefundRequest request) {
        Objects.requireNonNull(request, "Create refund request cannot be null");

        Refund refund = refundService.createRefund(
                request.paymentId(),
                request.amount(),
                request.reason(),
                request.description()
        );

        return RefundResponse.from(refund);
    }

    @Override
    public RefundResponse processRefund(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");

        refundService.processRefund(refundId);
        Refund refund = refundService.getRefund(refundId)
                .orElseThrow(() -> new IllegalArgumentException("Refund not found: " + refundId));

        return RefundResponse.from(refund);
    }

    @Override
    public RefundResponse getRefund(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");

        Refund refund = refundService.getRefund(refundId)
                .orElseThrow(() -> new IllegalArgumentException("Refund not found: " + refundId));

        return RefundResponse.from(refund);
    }

    @Override
    public List<RefundResponse> getRefundsForPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return refundService.getRefundsForPayment(paymentId).stream()
                .map(RefundResponse::from)
                .toList();
    }

    @Override
    public List<RefundResponse> getRefundsForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return refundService.getRefundsForTenant(tenantId).stream()
                .map(RefundResponse::from)
                .toList();
    }

    @Override
    public void cancelRefund(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");
        refundService.cancelRefund(refundId);
    }

    @Override
    public RefundStatusResponse checkRefundStatus(RefundId refundId) {
        Objects.requireNonNull(refundId, "Refund ID cannot be null");

        com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundStatusResponse status =
                refundService.checkRefundStatus(refundId);

        return new RefundStatusResponse(
                refundId.value().toString(),
                status.status(),
                status.status().name(),
                status.message(),
                Instant.now()
        );
    }

    @Override
    public Money calculateMaxRefundableAmount(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return refundService.calculateMaxRefundableAmount(paymentId);
    }
}
