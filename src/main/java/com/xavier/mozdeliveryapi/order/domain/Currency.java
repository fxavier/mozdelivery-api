package com.xavier.mozdeliveryapi.order.domain;

/**
 * Enumeration of supported currencies.
 */
public enum Currency {
    USD("USD", "$", 2),
    MZN("MZN", "MT", 2);
    
    private final String code;
    private final String symbol;
    private final int decimalPlaces;
    
    Currency(String code, String symbol, int decimalPlaces) {
        this.code = code;
        this.symbol = symbol;
        this.decimalPlaces = decimalPlaces;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public int getDecimalPlaces() {
        return decimalPlaces;
    }
    
    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}