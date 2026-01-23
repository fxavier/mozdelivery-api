package com.xavier.mozdeliveryapi.payment.domain.entity;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentStatus;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentCancelledEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentCompletedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentCreatedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentFailedEvent;
import com.xavier.mozdeliveryapi.payment.domain.event.PaymentProcessingStartedEvent;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.PaymentId;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Payment aggregate root representing a payment transaction.
 */
public class Payment extends AggregateRoot<PaymentId> {
    
    private final PaymentId id;
    private final TenantId tenantId;
    private final OrderId orderId;
    private final PaymentMethod method;
    private final Money amount;
    private final Currency currency;
    private PaymentStatus status;
    private String gatewayTransactionId;
    private String gatewayResponse;
    private BigDecimal exchangeRate;
    private final Instant createdAt;
    private Instant updatedAt;
    private String failureReason;
    
    // Constructor for creating new payment
    public Payment(PaymentId id, TenantId tenantId, OrderId orderId, 
                   PaymentMethod method, Money amount) {
        this.id = Objects.requireNonNull(id, "Payment ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.method = Objects.requireNonNull(method, "Payment method cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = amount.currency();
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(PaymentCreatedEvent.of(id, orderId, amount, method));
    }
    
    // Constructor for reconstituting from persistence
    public Payment(PaymentId id, TenantId tenantId, OrderId orderId, 
                   PaymentMethod method, Money amount, PaymentStatus status,
                   String gatewayTransactionId, String gatewayResponse,
                   BigDecimal exchangeRate, Instant createdAt, Instant updatedAt,
                   String failureReason) {
        this.id = Objects.requireNonNull(id, "Payment ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.method = Objects.requireNonNull(method, "Payment method cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = amount.currency();
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.gatewayTransactionId = gatewayTransactionId;
        this.gatewayResponse = gatewayResponse;
        this.exchangeRate = exchangeRate;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        this.failureReason = failureReason;
    }
    
    @Override
    protected PaymentId getId() {
        return id;
    }
    
    /**
     * Start payment processing.
     */
    public void startProcessing(String gatewayTransactionId) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Can only start processing from PENDING status");
        }
        
        this.status = PaymentStatus.PROCESSING;
        this.gatewayTransactionId = gatewayTransactionId;
        this.updatedAt = Instant.now();
        
        registerEvent(PaymentProcessingStartedEvent.of(id, orderId, gatewayTransactionId));
    }
    
    /**
     * Complete the payment successfully.
     */
    public void complete(String gatewayResponse) {
        if (status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Can only complete from PROCESSING status");
        }
        
        this.status = PaymentStatus.COMPLETED;
        this.gatewayResponse = gatewayResponse;
        this.updatedAt = Instant.now();
        
        registerEvent(PaymentCompletedEvent.of(id, orderId, amount));
    }
    
    /**
     * Fail the payment.
     */
    public void fail(String reason, String gatewayResponse) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot fail payment in final status: " + status);
        }
        
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.gatewayResponse = gatewayResponse;
        this.updatedAt = Instant.now();
        
        registerEvent(PaymentFailedEvent.of(id, orderId, reason));
    }
    
    /**
     * Cancel the payment.
     */
    public void cancel() {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot cancel payment in final status: " + status);
        }
        
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        registerEvent(PaymentCancelledEvent.of(id, orderId));
    }
    
    /**
     * Set exchange rate for multi-currency payments.
     */
    public void setExchangeRate(BigDecimal rate) {
        Objects.requireNonNull(rate, "Exchange rate cannot be null");
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        
        this.exchangeRate = rate;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Check if payment is completed.
     */
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Check if payment has failed.
     */
    public boolean hasFailed() {
        return status == PaymentStatus.FAILED;
    }
    
    /**
     * Check if payment can be refunded.
     */
    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED && method.supportsRefunds();
    }
    
    /**
     * Calculate amount in different currency using exchange rate.
     */
    public Money getAmountInCurrency(Currency targetCurrency) {
        if (currency == targetCurrency) {
            return amount;
        }
        
        if (exchangeRate == null) {
            throw new IllegalStateException("Exchange rate not set for currency conversion");
        }
        
        BigDecimal convertedAmount = amount.amount().multiply(exchangeRate);
        return Money.of(convertedAmount, targetCurrency);
    }
    
    // Getters
    public PaymentId getPaymentId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public OrderId getOrderId() { return orderId; }
    public PaymentMethod getMethod() { return method; }
    public Money getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public String getGatewayResponse() { return gatewayResponse; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getFailureReason() { return failureReason; }
}