package com.xavier.mozdeliveryapi.payment.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

import java.math.BigDecimal;


/**
 * Value object representing M-Pesa balance response.
 */
public record MPesaBalanceResponse(
    BigDecimal workingAccountBalance,
    BigDecimal utilityAccountBalance,
    BigDecimal chargesPaidAccountBalance
) implements ValueObject {
}