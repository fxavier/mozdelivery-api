package com.xavier.mozdeliveryapi.order.application.dto;

import java.util.Map;

import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;

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