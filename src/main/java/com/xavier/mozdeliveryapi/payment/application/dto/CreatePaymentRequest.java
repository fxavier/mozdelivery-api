package com.xavier.mozdeliveryapi.payment.application.dto;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod;
import jakarta.validation.constraints.NotNull;

/**
 * Request to create a new payment.
 */
public record CreatePaymentRequest(
    @NotNull OrderId orderId,
    @NotNull Money amount,
    @NotNull Currency currency,
    @NotNull PaymentMethod paymentMethod,
    String description
) {}