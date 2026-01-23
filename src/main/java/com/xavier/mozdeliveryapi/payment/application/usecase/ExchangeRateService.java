package com.xavier.mozdeliveryapi.payment.application.usecase;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.util.Optional;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.ExchangeRate;


/**
 * Domain service for exchange rate operations.
 */
public interface ExchangeRateService {
    
    /**
     * Get current exchange rate between two currencies.
     */
    Optional<ExchangeRate> getExchangeRate(Currency fromCurrency, Currency toCurrency);
    
    /**
     * Convert amount from one currency to another.
     */
    BigDecimal convertAmount(BigDecimal amount, Currency fromCurrency, Currency toCurrency);
    
    /**
     * Update exchange rate.
     */
    void updateExchangeRate(ExchangeRate exchangeRate);
    
    /**
     * Check if exchange rate is available for currency pair.
     */
    boolean isExchangeRateAvailable(Currency fromCurrency, Currency toCurrency);
}