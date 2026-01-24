package com.xavier.mozdeliveryapi.order.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.exception.OrderNotFoundException;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CreateOrderCommand;
import com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Implementation of OrderService with business logic.
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final MerchantRepository merchantRepository;
    private final VerticalBusinessRulesEngine verticalBusinessRulesEngine;
    
    public OrderServiceImpl(OrderRepository orderRepository, MerchantRepository merchantRepository,
                           VerticalBusinessRulesEngine verticalBusinessRulesEngine) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
        this.merchantRepository = Objects.requireNonNull(merchantRepository, "Merchant repository cannot be null");
        this.verticalBusinessRulesEngine = Objects.requireNonNull(verticalBusinessRulesEngine, 
            "Vertical business rules engine cannot be null");
    }
    
    @Override
    public Order createOrder(CreateOrderCommand command) {
        validateOrderCreation(command);
        
        // Calculate total amount from items
        Money totalAmount = calculateTotalAmount(command.items(), command.currency());
        
        // Create payment info
        PaymentInfo paymentInfo = PaymentInfo.pending(command.paymentMethod(), totalAmount);
        
        // Create order
        Order order = new Order(
            OrderId.generate(),
            command.merchantId(),
            command.customerId(),
            command.items(),
            command.deliveryAddress(),
            paymentInfo
        );
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order updateOrderStatus(OrderId orderId, OrderStatus status) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.updateStatus(status);
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order cancelOrder(OrderId orderId, CancellationReason reason, String details) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(reason, "Cancellation reason cannot be null");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.cancel(reason, details);
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order cancelOrder(OrderId orderId, CancellationReason reason) {
        return cancelOrder(orderId, reason, null);
    }
    
    @Override
    public List<Order> findOrdersByMerchant(MerchantId merchantId, OrderFilter filter) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        
        // For now, implement basic filtering
        if (filter.status().isPresent()) {
            return orderRepository.findByMerchantIdAndStatus(merchantId, filter.status().get());
        } else if (filter.customerId().isPresent()) {
            return orderRepository.findByCustomerIdAndMerchantId(filter.customerId().get(), merchantId);
        } else {
            return orderRepository.findByMerchantId(merchantId);
        }
    }
    
    @Override
    public void validateOrderCreation(CreateOrderCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        
        // Validate merchant exists and can process orders
        Merchant merchant = merchantRepository.findById(command.merchantId())
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + command.merchantId()));
        
        if (!merchant.canProcessOrders()) {
            throw new IllegalArgumentException("Merchant cannot process orders: " + merchant.getStatus());
        }
        
        // Validate all items have the same currency
        Currency expectedCurrency = command.currency();
        for (OrderItem item : command.items()) {
            if (!item.totalPrice().currency().equals(expectedCurrency)) {
                throw new IllegalArgumentException("All items must have the same currency as the order");
            }
        }
        
        // Apply vertical-specific validation
        ValidationResult verticalValidation = verticalBusinessRulesEngine.validateOrderItems(
            command.items(), merchant.getVertical());
        
        if (!verticalValidation.isValid()) {
            throw new IllegalArgumentException("Vertical validation failed: " + 
                verticalValidation.getFirstErrorMessage());
        }
        
        // Additional validation can be added here
        // - Check if delivery address is within service area
        // - Validate payment method is supported by tenant
        // - Check inventory availability
    }
    
    @Override
    public Money calculateDeliveryFee(MerchantId merchantId, DeliveryAddress address, List<OrderItem> items) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");
        
        // Simple delivery fee calculation - can be enhanced with complex logic
        // For now, return a fixed fee based on currency
        Currency currency = items.isEmpty() ? Currency.USD : items.get(0).totalPrice().currency();
        
        return switch (currency) {
            case USD -> Money.of(BigDecimal.valueOf(2.50), Currency.USD);
            case MZN -> Money.of(BigDecimal.valueOf(150.00), Currency.MZN);
        };
    }
    
    private Money calculateTotalAmount(List<OrderItem> items, Currency currency) {
        Money total = Money.zero(currency);
        
        for (OrderItem item : items) {
            if (!item.totalPrice().currency().equals(currency)) {
                throw new IllegalArgumentException("Item currency does not match order currency");
            }
            total = total.add(item.totalPrice());
        }
        
        return total;
    }
}
