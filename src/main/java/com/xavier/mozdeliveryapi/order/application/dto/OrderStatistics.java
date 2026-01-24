package com.xavier.mozdeliveryapi.order.application.dto;

import java.util.Map;

import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Statistics about orders for a merchant.
 */
public record OrderStatistics(
    MerchantId merchantId,
    long totalOrders,
    Map<OrderStatus, Long> ordersByStatus,
    Money totalRevenue,
    Money averageOrderValue,
    long ordersToday,
    long ordersThisWeek,
    long ordersThisMonth
) {
    
    public static OrderStatistics empty(MerchantId merchantId, Currency currency) {
        return new OrderStatistics(
            merchantId,
            0L,
            Map.of(),
            Money.zero(currency),
            Money.zero(currency),
            0L,
            0L,
            0L
        );
    }
}