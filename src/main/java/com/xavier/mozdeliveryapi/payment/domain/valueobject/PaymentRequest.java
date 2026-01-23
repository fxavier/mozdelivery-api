package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.util.Map;
import java.util.Objects;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.payment.domain.entity.Payment;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Value object representing a payment request.
 */
public record PaymentRequest(
    PaymentId paymentId,
    TenantId tenantId,
    OrderId orderId,
    PaymentMethod method,
    Money amount,
    String customerReference,
    Map<String, String> additionalData
) implements ValueObject {
    
    public PaymentRequest {
        Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(method, "Payment method cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        
        // Create defensive copy of additional data
        additionalData = additionalData != null ? Map.copyOf(additionalData) : Map.of();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private PaymentId paymentId;
        private TenantId tenantId;
        private OrderId orderId;
        private PaymentMethod method;
        private Money amount;
        private String customerReference;
        private Map<String, String> additionalData = Map.of();
        
        public Builder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }
        
        public Builder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }
        
        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }
        
        public Builder method(PaymentMethod method) {
            this.method = method;
            return this;
        }
        
        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder customerReference(String customerReference) {
            this.customerReference = customerReference;
            return this;
        }
        
        public Builder additionalData(Map<String, String> additionalData) {
            this.additionalData = additionalData != null ? additionalData : Map.of();
            return this;
        }
        
        public PaymentRequest build() {
            return new PaymentRequest(paymentId, tenantId, orderId, method, amount, 
                                    customerReference, additionalData);
        }
    }
}