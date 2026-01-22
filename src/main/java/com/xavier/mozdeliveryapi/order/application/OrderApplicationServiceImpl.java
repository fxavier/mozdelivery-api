package com.xavier.mozdeliveryapi.order.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.order.domain.CreateOrderCommand;
import com.xavier.mozdeliveryapi.order.domain.Currency;
import com.xavier.mozdeliveryapi.order.domain.DeliveryAddress;
import com.xavier.mozdeliveryapi.order.domain.Money;
import com.xavier.mozdeliveryapi.order.domain.Order;
import com.xavier.mozdeliveryapi.order.domain.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.OrderNotFoundException;
import com.xavier.mozdeliveryapi.order.domain.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.OrderService;
import com.xavier.mozdeliveryapi.order.domain.OrderStatus;
import com.xavier.mozdeliveryapi.order.domain.PaymentInfo;
import com.xavier.mozdeliveryapi.order.domain.PaymentStatus;
import com.xavier.mozdeliveryapi.shared.application.DomainEventPublisher;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Implementation of OrderApplicationService.
 */
@Service
@Transactional
public class OrderApplicationServiceImpl implements OrderApplicationService {
    
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    
    public OrderApplicationServiceImpl(OrderService orderService, 
                                     OrderRepository orderRepository,
                                     DomainEventPublisher eventPublisher) {
        this.orderService = Objects.requireNonNull(orderService, "Order service cannot be null");
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        // Convert request to domain command
        CreateOrderCommand command = convertToCommand(request);
        
        // Create order through domain service
        Order order = orderService.createOrder(command);
        
        // Publish domain events
        publishDomainEvents(order);
        
        return OrderResponse.from(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return OrderResponse.from(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForTenant(TenantId tenantId, OrderFilter filter) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        
        List<Order> orders = orderService.findOrdersByTenant(tenantId, filter);
        
        return orders.stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public OrderResponse updateOrderStatus(OrderId orderId, OrderStatus status) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        Order order = orderService.updateOrderStatus(orderId, status);
        
        // Publish domain events
        publishDomainEvents(order);
        
        return OrderResponse.from(order);
    }
    
    @Override
    public OrderResponse cancelOrder(OrderId orderId, CancellationRequest request) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");
        
        Order order = orderService.cancelOrder(orderId, request.reason(), request.details());
        
        // Publish domain events
        publishDomainEvents(order);
        
        return OrderResponse.from(order);
    }
    
    @Override
    public OrderResponse confirmPayment(OrderId orderId, PaymentConfirmationRequest request) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // Update payment info with confirmation
        PaymentInfo updatedPayment = order.getPaymentInfo()
            .withStatus(PaymentStatus.COMPLETED)
            .withReference(request.paymentReference());
        
        order.updatePaymentInfo(updatedPayment);
        
        Order savedOrder = orderRepository.save(order);
        
        // Publish domain events
        publishDomainEvents(savedOrder);
        
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    public OrderResponse failPayment(OrderId orderId, PaymentFailureRequest request) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // Update payment info with failure
        PaymentInfo updatedPayment = order.getPaymentInfo()
            .withStatus(PaymentStatus.FAILED);
        
        order.updatePaymentInfo(updatedPayment);
        
        Order savedOrder = orderRepository.save(order);
        
        // Publish domain events
        publishDomainEvents(savedOrder);
        
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        // For now, return empty statistics
        // This can be enhanced with actual calculations
        return OrderStatistics.empty(tenantId, Currency.USD);
    }
    
    private CreateOrderCommand convertToCommand(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
            .map(itemRequest -> OrderItem.of(
                itemRequest.productId(),
                itemRequest.productName(),
                itemRequest.quantity(),
                Money.of(itemRequest.unitPrice(), request.currency())
            ))
            .collect(Collectors.toList());
        
        DeliveryAddress address = new DeliveryAddress(
            request.deliveryAddress().street(),
            request.deliveryAddress().city(),
            request.deliveryAddress().district(),
            request.deliveryAddress().postalCode(),
            request.deliveryAddress().country(),
            request.deliveryAddress().latitude(),
            request.deliveryAddress().longitude(),
            request.deliveryAddress().deliveryInstructions()
        );
        
        return new CreateOrderCommand(
            request.tenantId(),
            request.customerId(),
            items,
            address,
            request.paymentMethod(),
            request.currency()
        );
    }
    
    private void publishDomainEvents(Order order) {
        order.getDomainEvents().forEach(eventPublisher::publish);
        order.clearDomainEvents();
    }
}