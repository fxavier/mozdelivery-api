package com.xavier.mozdeliveryapi.payment.domain.valueobject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;


/**
 * Value object representing the result of an M-Pesa B2C payment.
 */
public record MPesaB2CResult(
    boolean success,
    String conversationId,
    String originatorConversationId,
    String responseCode,
    String responseDescription
) implements ValueObject {
}