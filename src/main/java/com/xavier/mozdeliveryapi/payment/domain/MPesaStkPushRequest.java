package com.xavier.mozdeliveryapi.payment.domain;

import java.util.Objects;

import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing an M-Pesa STK Push request.
 */
public record MPesaStkPushRequest(
    String phoneNumber,
    Money amount,
    String accountReference,
    String transactionDesc
) implements ValueObject {
    
    public MPesaStkPushRequest {
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(accountReference, "Account reference cannot be null");
        Objects.requireNonNull(transactionDesc, "Transaction description cannot be null");
    }
}