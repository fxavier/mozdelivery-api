package com.xavier.mozdeliveryapi.order.infra.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for Order persistence.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "items", nullable = false, columnDefinition = "jsonb")
    private List<OrderItemData> items;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delivery_address", nullable = false, columnDefinition = "jsonb")
    private DeliveryAddressData deliveryAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_info", nullable = false, columnDefinition = "jsonb")
    private PaymentInfoData paymentInfo;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected OrderEntity() {}
    
    // Constructor
    public OrderEntity(UUID id, UUID tenantId, UUID customerId, List<OrderItemData> items,
                      DeliveryAddressData deliveryAddress, OrderStatus status, 
                      PaymentInfoData paymentInfo, BigDecimal totalAmount, Currency currency,
                      Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.paymentInfo = paymentInfo;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    
    public List<OrderItemData> getItems() { return items; }
    public void setItems(List<OrderItemData> items) { this.items = items; }
    
    public DeliveryAddressData getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddressData deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public PaymentInfoData getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(PaymentInfoData paymentInfo) { this.paymentInfo = paymentInfo; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    // Data classes for JSON serialization
    public static record OrderItemData(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String currency
    ) {}
    
    public static record DeliveryAddressData(
        String street,
        String city,
        String district,
        String postalCode,
        String country,
        double latitude,
        double longitude,
        String deliveryInstructions
    ) {}
    
    public static record PaymentInfoData(
        String method,
        String paymentReference,
        BigDecimal amount,
        String currency,
        String status
    ) {}
}