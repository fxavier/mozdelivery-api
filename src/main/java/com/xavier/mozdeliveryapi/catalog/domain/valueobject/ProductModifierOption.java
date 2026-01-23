package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a product modifier option.
 */
public record ProductModifierOption(
    String name,
    String description,
    Money priceAdjustment,
    boolean available
) implements ValueObject {
    
    public ProductModifierOption {
        Objects.requireNonNull(name, "Option name cannot be null");
        Objects.requireNonNull(priceAdjustment, "Price adjustment cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Option name cannot be empty");
        }
    }
    
    /**
     * Create an option with no price adjustment.
     */
    public static ProductModifierOption free(String name, String description, boolean available) {
        return new ProductModifierOption(name, description, Money.zero(com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency.USD), available);
    }
    
    /**
     * Create an option with price adjustment.
     */
    public static ProductModifierOption withPrice(String name, String description, Money priceAdjustment, boolean available) {
        return new ProductModifierOption(name, description, priceAdjustment, available);
    }
}