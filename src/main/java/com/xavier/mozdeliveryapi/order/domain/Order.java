package com.xavier.mozdeliveryapi.order.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.AggregateRoot;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Order aggregate root representing a customer order.
 */
public class Order extends AggregateRoot<OrderId> {
    
    private final OrderId id;
    private final TenantId tenantId;
    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final DeliveryAddress deliveryAddress;
    private OrderStatus status;
    private PaymentInfo paymentInfo;
    private final Money totalAmount;
    private final Currency currency;
    private final Instant createdAt;
    private Instant updatedAt;
    
    // Constructor for creating new order
    public Order(OrderId id, TenantId tenantId, CustomerId customerId, 
                 List<OrderItem> items, DeliveryAddress deliveryAddress, 
                 PaymentInfo paymentInfo) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = validateItems(items);
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "Delivery address cannot be null");
        this.paymentInfo = Objects.requireNonNull(paymentInfo, "Payment info cannot be null");
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotalAmount(items);
        this.currency = this.totalAmount.currency();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        
        // Validate payment amount matches order total
        if (!paymentInfo.amount().equals(totalAmount)) {
            throw new IllegalArgumentException("Payment amount must match order total");
        }
        
        // Register domain event
        registerEvent(OrderCreatedEvent.of(id, tenantId, customerId, totalAmount));
    }
    
    // Constructor for reconstituting from persistence
    public Order(OrderId id, TenantId tenantId, CustomerId customerId, 
                 List<OrderItem> items, DeliveryAddress deliveryAddress, 
                 OrderStatus status, PaymentInfo paymentInfo, Money totalAmount, 
                 Currency currency, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = validateItems(items);
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "Delivery address cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.paymentInfo = Objects.requireNonNull(paymentInfo, "Payment info cannot be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    @Override
    protected OrderId getId() {
        return id;
    }
    
    /**
     * Update order status with validation.
     */
    public void updateStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        if (status == newStatus) {
            return; // No change needed
        }
        
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus));
        }
        
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = Instant.now();
        
        registerEvent(OrderStatusChangedEvent.of(id, oldStatus, newStatus));
    }
    
    /**
     * Cancel the order with a reason.
     */
    public void cancel(CancellationReason reason, String details) {
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
        
        if (!status.canBeCancelled()) {
            throw new IllegalStateException(
                String.format("Cannot cancel order in status %s", status));
        }
        
        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
        
        registerEvent(OrderStatusChangedEvent.of(id, oldStatus, OrderStatus.CANCELLED));
        registerEvent(OrderCancelledEvent.of(id, reason, details));
    }
    
    /**
     * Cancel the order with a reason (no details).
     */
    public void cancel(CancellationReason reason) {
        cancel(reason, null);
    }
    
    /**
     * Update payment information.
     */
    public void updatePaymentInfo(PaymentInfo newPaymentInfo) {
        Objects.requireNonNull(newPaymentInfo, "Payment info cannot be null");
        
        // Validate payment amount still matches order total
        if (!newPaymentInfo.amount().equals(totalAmount)) {
            throw new IllegalArgumentException("Payment amount must match order total");
        }
        
        this.paymentInfo = newPaymentInfo;
        this.updatedAt = Instant.now();
        
        // If payment is completed, update order status
        if (newPaymentInfo.isPaid() && status == OrderStatus.PAYMENT_PROCESSING) {
            updateStatus(OrderStatus.PAYMENT_CONFIRMED);
        } else if (newPaymentInfo.isFailed() && status == OrderStatus.PAYMENT_PROCESSING) {
            cancel(CancellationReason.PAYMENT_FAILED);
        }
    }
    
    /**
     * Check if the order can be cancelled.
     */
    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }
    
    /**
     * Check if the order is active.
     */
    public boolean isActive() {
        return status.isActive();
    }
    
    /**
     * Check if the order is completed.
     */
    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED;
    }
    
    /**
     * Get the number of items in the order.
     */
    public int getItemCount() {
        return items.stream().mapToInt(OrderItem::quantity).sum();
    }
    
    private List<OrderItem> validateItems(List<OrderItem> items) {
        Objects.requireNonNull(items, "Items cannot be null");
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        // Create defensive copy
        return new ArrayList<>(items);
    }
    
    private Money calculateTotalAmount(List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate total for empty order");
        }
        
        // All items must have the same currency
        Currency orderCurrency = items.get(0).totalPrice().currency();
        Money total = Money.zero(orderCurrency);
        
        for (OrderItem item : items) {
            if (!item.totalPrice().currency().equals(orderCurrency)) {
                throw new IllegalArgumentException("All items must have the same currency");
            }
            total = total.add(item.totalPrice());
        }
        
        return total;
    }
    
    // Getters
    public OrderId getOrderId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public OrderStatus getStatus() { return status; }
    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public Money getTotalAmount() { return totalAmount; }
    public Currency getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}