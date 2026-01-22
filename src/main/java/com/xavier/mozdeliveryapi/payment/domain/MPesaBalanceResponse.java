package com.xavier.mozdeliveryapi.payment.domain;

import java.math.BigDecimal;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing M-Pesa balance response.
 */
public record MPesaBalanceResponse(
    BigDecimal workingAccountBalance,
    BigDecimal utilityAccountBalance,
    BigDecimal chargesPaidAccountBalance
) implements ValueObject {
}