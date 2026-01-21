package com.xavier.mozdeliveryapi.tenant.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Value object representing tenant-specific configuration settings.
 */
public record TenantConfiguration(
    BigDecimal deliveryFee,
    BigDecimal minimumOrderAmount,
    Duration maxDeliveryTime,
    boolean acceptsCashPayments,
    boolean acceptsCardPayments,
    boolean acceptsMobilePayments,
    Map<String, Object> customSettings
) implements ValueObject {
    
    public TenantConfiguration {
        Objects.requireNonNull(deliveryFee, "Delivery fee cannot be null");
        Objects.requireNonNull(minimumOrderAmount, "Minimum order amount cannot be null");
        Objects.requireNonNull(maxDeliveryTime, "Max delivery time cannot be null");
        Objects.requireNonNull(customSettings, "Custom settings cannot be null");
        
        if (deliveryFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Delivery fee cannot be negative");
        }
        
        if (minimumOrderAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum order amount cannot be negative");
        }
        
        if (maxDeliveryTime.isNegative() || maxDeliveryTime.isZero()) {
            throw new IllegalArgumentException("Max delivery time must be positive");
        }
    }
    
    /**
     * Create default configuration for a vertical.
     */
    public static TenantConfiguration defaultFor(Vertical vertical) {
        return switch (vertical) {
            case RESTAURANT -> new TenantConfiguration(
                new BigDecimal("50.00"), // 50 MZN delivery fee
                new BigDecimal("200.00"), // 200 MZN minimum order
                Duration.ofMinutes(45),
                true, true, true,
                Map.of("preparationTime", Duration.ofMinutes(20))
            );
            case GROCERY -> new TenantConfiguration(
                new BigDecimal("75.00"),
                new BigDecimal("300.00"),
                Duration.ofMinutes(60),
                true, true, true,
                Map.of("perishableHandling", true)
            );
            case PHARMACY -> new TenantConfiguration(
                new BigDecimal("30.00"),
                new BigDecimal("100.00"),
                Duration.ofMinutes(30),
                false, true, true, // No cash for pharmacy
                Map.of("prescriptionRequired", true, "ageVerification", true)
            );
            default -> new TenantConfiguration(
                new BigDecimal("60.00"),
                new BigDecimal("250.00"),
                Duration.ofMinutes(50),
                true, true, true,
                Map.of()
            );
        };
    }
    
    /**
     * Check if any payment method is accepted.
     */
    public boolean acceptsAnyPayment() {
        return acceptsCashPayments || acceptsCardPayments || acceptsMobilePayments;
    }
}