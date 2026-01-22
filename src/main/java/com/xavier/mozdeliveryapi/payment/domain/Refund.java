package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.RefundReason;
import com.xavier.mozdeliveryapi.order.domain.RefundStatus;
import com.xavier.mozdeliveryapi.shared.domain.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.time.Instant;
import java.util.Objects;

/**
 * Refund aggregate root representing a payment refund.
 */
public class Refund extends AggregateRoot<RefundId> {
    
    private final RefundId id;
    private final TenantId tenantId;
    private final PaymentId paymentId;
    private final Money amount;
    private final RefundReason reason;
    private final String description;
    private RefundStatus status;
    private String gatewayRefundId;
    private String gatewayResponse;
    private final Instant createdAt;
    private Instant updatedAt;
    private String failureReason;
    
    // Constructor for creating new refund
    public Refund(RefundId id, TenantId tenantId, PaymentId paymentId, 
                  Money amount, RefundReason reason, String description) {
        this.id = Objects.requireNonNull(id, "Refund ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.paymentId = Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.description = description;
        this.status = RefundStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Register domain event
        registerEvent(RefundCreatedEvent.of(id, paymentId, amount, reason));
    }
    
    // Constructor for reconstituting from persistence
    public Refund(RefundId id, TenantId tenantId, PaymentId paymentId, 
                  Money amount, RefundReason reason, String description,
                  RefundStatus status, String gatewayRefundId, String gatewayResponse,
                  Instant createdAt, Instant updatedAt, String failureReason) {
        this.id = Objects.requireNonNull(id, "Refund ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.paymentId = Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.description = description;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.gatewayRefundId = gatewayRefundId;
        this.gatewayResponse = gatewayResponse;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        this.failureReason = failureReason;
    }
    
    @Override
    protected RefundId getId() {
        return id;
    }
    
    /**
     * Start refund processing.
     */
    public void startProcessing(String gatewayRefundId) {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("Can only start processing from PENDING status");
        }
        
        this.status = RefundStatus.PROCESSING;
        this.gatewayRefundId = gatewayRefundId;
        this.updatedAt = Instant.now();
        
        registerEvent(RefundProcessingStartedEvent.of(id, paymentId, gatewayRefundId));
    }
    
    /**
     * Complete the refund successfully.
     */
    public void complete(String gatewayResponse) {
        if (status != RefundStatus.PROCESSING) {
            throw new IllegalStateException("Can only complete from PROCESSING status");
        }
        
        this.status = RefundStatus.COMPLETED;
        this.gatewayResponse = gatewayResponse;
        this.updatedAt = Instant.now();
        
        registerEvent(RefundCompletedEvent.of(id, paymentId, amount));
    }
    
    /**
     * Fail the refund.
     */
    public void fail(String reason, String gatewayResponse) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot fail refund in final status: " + status);
        }
        
        this.status = RefundStatus.FAILED;
        this.failureReason = reason;
        this.gatewayResponse = gatewayResponse;
        this.updatedAt = Instant.now();
        
        registerEvent(RefundFailedEvent.of(id, paymentId, reason));
    }
    
    /**
     * Cancel the refund.
     */
    public void cancel() {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot cancel refund in final status: " + status);
        }
        
        this.status = RefundStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        registerEvent(RefundCancelledEvent.of(id, paymentId));
    }
    
    /**
     * Check if refund is completed.
     */
    public boolean isCompleted() {
        return status == RefundStatus.COMPLETED;
    }
    
    /**
     * Check if refund has failed.
     */
    public boolean hasFailed() {
        return status == RefundStatus.FAILED;
    }
    
    /**
     * Check if refund is in progress.
     */
    public boolean isInProgress() {
        return status == RefundStatus.PROCESSING;
    }
    
    /**
     * Check if refund can be cancelled.
     */
    public boolean canBeCancelled() {
        return status == RefundStatus.PENDING || status == RefundStatus.PROCESSING;
    }
    
    // Getters
    public RefundId getRefundId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public PaymentId getPaymentId() { return paymentId; }
    public Money getAmount() { return amount; }
    public RefundReason getReason() { return reason; }
    public String getDescription() { return description; }
    public RefundStatus getStatus() { return status; }
    public String getGatewayRefundId() { return gatewayRefundId; }
    public String getGatewayResponse() { return gatewayResponse; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getFailureReason() { return failureReason; }
}