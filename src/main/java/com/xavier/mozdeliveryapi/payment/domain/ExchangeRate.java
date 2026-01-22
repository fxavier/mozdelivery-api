package com.xavier.mozdeliveryapi.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an exchange rate between two currencies.
 */
public record ExchangeRate(
    Currency fromCurrency,
    Currency toCurrency,
    BigDecimal rate,
    Instant effectiveDate
) implements ValueObject {
    
    public ExchangeRate {
        Objects.requireNonNull(fromCurrency, "From currency cannot be null");
        Objects.requireNonNull(toCurrency, "To currency cannot be null");
        Objects.requireNonNull(rate, "Rate cannot be null");
        Objects.requireNonNull(effectiveDate, "Effective date cannot be null");
        
        if (fromCurrency == toCurrency) {
            throw new IllegalArgumentException("From and to currencies cannot be the same");
        }
        
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
    }
    
    /**
     * Convert an amount using this exchange rate.
     */
    public BigDecimal convert(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        return amount.multiply(rate);
    }
    
    /**
     * Get the inverse exchange rate.
     */
    public ExchangeRate inverse() {
        BigDecimal inverseRate = BigDecimal.ONE.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
        return new ExchangeRate(toCurrency, fromCurrency, inverseRate, effectiveDate);
    }
    
    /**
     * Check if this exchange rate is still valid (not too old).
     */
    public boolean isValid(Instant currentTime, long maxAgeMinutes) {
        return effectiveDate.plusSeconds(maxAgeMinutes * 60).isAfter(currentTime);
    }
}