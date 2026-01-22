package com.xavier.mozdeliveryapi.order.application;

import java.util.Map;

import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.OrderStatus;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Statistics about orders for a tenant.
 */
public record OrderStatistics(
    TenantId tenantId,
    long totalOrders,
    Map<OrderStatus, Long> ordersByStatus,
    Money totalRevenue,
    Money averageOrderValue,
    long ordersToday,
    long ordersThisWeek,
    long ordersThisMonth
) {
    
    public static OrderStatistics empty(TenantId tenantId, Currency currency) {
        return new OrderStatistics(
            tenantId,
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