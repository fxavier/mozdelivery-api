package com.xavier.mozdeliveryapi.payment.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.payment.application.usecase.port.PaymentGateway;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;

/**
 * Factory for selecting the appropriate payment gateway based on payment method.
 */
@Component
public class PaymentGatewayFactory {
    
    private final List<PaymentGateway> gateways;
    
    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }
    
    /**
     * Get the appropriate payment gateway for the given payment method.
     */
    public Optional<PaymentGateway> getGateway(PaymentMethod paymentMethod) {
        return gateways.stream()
                .filter(gateway -> gateway.supportsPaymentMethod(paymentMethod))
                .findFirst();
    }
    
    /**
     * Get all available payment gateways.
     */
    public List<PaymentGateway> getAllGateways() {
        return List.copyOf(gateways);
    }
    
    /**
     * Check if a payment method is supported by any gateway.
     */
    public boolean isPaymentMethodSupported(PaymentMethod paymentMethod) {
        return gateways.stream()
                .anyMatch(gateway -> gateway.supportsPaymentMethod(paymentMethod));
    }
}