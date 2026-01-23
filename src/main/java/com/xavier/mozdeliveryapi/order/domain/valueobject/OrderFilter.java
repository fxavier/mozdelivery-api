package com.xavier.mozdeliveryapi.order.domain.valueobject;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;

import java.time.Instant;
import java.util.Optional;


/**
 * Filter criteria for order queries.
 */
public record OrderFilter(
    Optional<OrderStatus> status,
    Optional<CustomerId> customerId,
    Optional<Instant> createdAfter,
    Optional<Instant> createdBefore,
    Optional<Currency> currency,
    int page,
    int size
) implements ValueObject {
    
    public OrderFilter {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Size cannot exceed 100");
        }
    }
    
    public static OrderFilter empty() {
        return new OrderFilter(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            0,
            20
        );
    }
    
    public static OrderFilter byStatus(OrderStatus status) {
        return new OrderFilter(
            Optional.of(status),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            0,
            20
        );
    }
    
    public static OrderFilter byCustomer(CustomerId customerId) {
        return new OrderFilter(
            Optional.empty(),
            Optional.of(customerId),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            0,
            20
        );
    }
    
    public OrderFilter withPage(int page, int size) {
        return new OrderFilter(status, customerId, createdAfter, createdBefore, currency, page, size);
    }
}