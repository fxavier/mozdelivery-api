package com.xavier.mozdeliveryapi.order.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a monetary amount.
 */
public record Money(BigDecimal amount, Currency currency) implements ValueObject {
    
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // Ensure proper scale for currency
        amount = amount.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_UP);
    }
    
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
    
    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    /**
     * Add another money amount. Must be same currency.
     */
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }
    
    /**
     * Subtract another money amount. Must be same currency.
     */
    public Money subtract(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result, currency);
    }
    
    /**
     * Multiply by a factor.
     */
    public Money multiply(BigDecimal factor) {
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        return new Money(amount.multiply(factor), currency);
    }
    
    /**
     * Check if this amount is greater than another.
     */
    public boolean isGreaterThan(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare different currencies");
        }
        return amount.compareTo(other.amount) > 0;
    }
    
    /**
     * Check if this amount is zero.
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount.toPlainString();
    }
}