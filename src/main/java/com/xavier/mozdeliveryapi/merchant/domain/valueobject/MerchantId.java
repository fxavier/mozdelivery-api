package com.xavier.mozdeliveryapi.merchant.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a merchant identifier.
 */
public record MerchantId(UUID value) implements ValueObject {
    
    public MerchantId {
        Objects.requireNonNull(value, "Merchant ID cannot be null");
    }
    
    public static MerchantId generate() {
        return new MerchantId(UUID.randomUUID());
    }
    
    public static MerchantId of(String value) {
        return new MerchantId(UUID.fromString(value));
    }
    
    public static MerchantId of(UUID value) {
        return new MerchantId(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}