package com.xavier.mozdeliveryapi.payment.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

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