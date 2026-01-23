package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing M-Pesa transaction status.
 */
public record MPesaTransactionStatus(
    String resultCode,
    String resultDesc,
    String amount,
    String mpesaReceiptNumber
) implements ValueObject {
}