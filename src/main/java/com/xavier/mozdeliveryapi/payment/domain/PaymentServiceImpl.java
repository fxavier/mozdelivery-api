package com.xavier.mozdeliveryapi.payment.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.payment.infrastructure.PaymentGatewayFactory;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Domain implementation of PaymentService.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayFactory gatewayFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentGatewayFactory gatewayFactory) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "Payment repository cannot be null");
        this.gatewayFactory = Objects.requireNonNull(gatewayFactory, "Gateway factory cannot be null");
    }

    @Override
    public Payment createPayment(PaymentRequest request) {
        Objects.requireNonNull(request, "Payment request cannot be null");

        Payment payment = new Payment(
                request.paymentId(),
                request.tenantId(),
                request.orderId(),
                request.method(),
                request.amount()
        );

        return paymentRepository.save(payment);
    }

    @Override
    public PaymentResult processPayment(PaymentId paymentId) {
        Payment payment = getPaymentOrThrow(paymentId);

        PaymentGateway gateway = gatewayFactory.getGateway(payment.getMethod())
                .orElseThrow(() -> new IllegalStateException(
                        "No gateway found for payment method: " + payment.getMethod()));

        PaymentRequest request = buildRequest(payment);
        PaymentResult result = gateway.processPayment(request);

        applyResult(payment, result);
        paymentRepository.save(payment);

        return result;
    }

    @Override
    public PaymentStatusResponse checkPaymentStatus(PaymentId paymentId) {
        Payment payment = getPaymentOrThrow(paymentId);
        String gatewayTransactionId = payment.getGatewayTransactionId();

        if (gatewayTransactionId == null || gatewayTransactionId.isBlank()) {
            return PaymentStatusResponse.of(payment.getStatus(), "No gateway transaction id");
        }

        PaymentGateway gateway = gatewayFactory.getGateway(payment.getMethod())
                .orElseThrow(() -> new IllegalStateException(
                        "No gateway found for payment method: " + payment.getMethod()));

        PaymentStatusResponse response = gateway.checkPaymentStatus(gatewayTransactionId);
        applyStatus(payment, response.status(), gatewayTransactionId, response.message(), null);
        paymentRepository.save(payment);

        return response;
    }

    @Override
    public void cancelPayment(PaymentId paymentId) {
        Payment payment = getPaymentOrThrow(paymentId);
        payment.cancel();
        paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsForOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payment> getPaymentsForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        return paymentRepository.findByTenantId(tenantId);
    }

    private Payment getPaymentOrThrow(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }

    private PaymentRequest buildRequest(Payment payment) {
        return PaymentRequest.builder()
                .paymentId(payment.getPaymentId())
                .tenantId(payment.getTenantId())
                .orderId(payment.getOrderId())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .customerReference(payment.getOrderId().value().toString())
                .additionalData(Map.of())
                .build();
    }

    private void applyResult(Payment payment, PaymentResult result) {
        if (result == null) {
            return;
        }

        applyStatus(
                payment,
                result.status(),
                result.gatewayTransactionId(),
                result.message(),
                result.errorCode()
        );
    }

    private void applyStatus(Payment payment, PaymentStatus status, String gatewayTransactionId,
                             String message, String errorCode) {
        if (status == null || payment.getStatus().isFinal()) {
            return;
        }

        switch (status) {
            case PROCESSING -> {
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    payment.startProcessing(gatewayTransactionId);
                }
            }
            case COMPLETED -> {
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    payment.startProcessing(gatewayTransactionId);
                }
                if (payment.getStatus() == PaymentStatus.PROCESSING) {
                    payment.complete(message);
                }
            }
            case FAILED -> {
                if (payment.getStatus() == PaymentStatus.PENDING && gatewayTransactionId != null) {
                    payment.startProcessing(gatewayTransactionId);
                }
                if (!payment.getStatus().isFinal()) {
                    payment.fail(errorCode != null ? errorCode : "PAYMENT_FAILED", message);
                }
            }
            case CANCELLED -> {
                if (!payment.getStatus().isFinal()) {
                    payment.cancel();
                }
            }
            default -> {
                // No-op for other statuses.
            }
        }
    }
}
