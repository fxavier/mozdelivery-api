package com.xavier.mozdeliveryapi.payment.application;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.PaymentMethod;
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