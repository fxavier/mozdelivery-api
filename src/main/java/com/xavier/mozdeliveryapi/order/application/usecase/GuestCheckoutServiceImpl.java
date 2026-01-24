package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.exception.OrderNotFoundException;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of guest checkout service.
 */
@Service
@Transactional
public class GuestCheckoutServiceImpl implements GuestCheckoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestCheckoutServiceImpl.class);
    
    private final OrderRepository orderRepository;
    
    public GuestCheckoutServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
    }
    
    @Override
    public Order createGuestOrder(GuestOrderCommand command) {
        Objects.requireNonNull(command, "Guest order command cannot be null");
        
        logger.info("Creating guest order for merchant: {}", command.merchantId());
        
        // Validate the command
        validateGuestOrderCreation(command);
        
        // Generate new order ID
        OrderId orderId = OrderId.generate();
        
        // Create the order
        Order order = new Order(
            orderId,
            command.merchantId(),
            command.guestInfo(),
            command.items(),
            command.deliveryAddress(),
            command.paymentInfo()
        );
        
        // Save the order
        Order savedOrder = orderRepository.save(order);
        
        logger.info("Guest order created successfully: {}", orderId);
        
        return savedOrder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Order findOrderByTrackingToken(GuestTrackingToken token) {
        Objects.requireNonNull(token, "Tracking token cannot be null");
        
        if (!token.isValid()) {
            throw new IllegalArgumentException("Tracking token has expired");
        }
        
        logger.debug("Finding order by tracking token: {}", token);
        
        Optional<Order> order = orderRepository.findByGuestTrackingToken(token);
        
        if (order.isEmpty()) {
            throw new OrderNotFoundException(OrderId.generate()); // Placeholder ID
        }
        
        return order.get();
    }
    
    @Override
    public void validateGuestOrderCreation(GuestOrderCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        Objects.requireNonNull(command.merchantId(), "Merchant ID cannot be null");
        Objects.requireNonNull(command.guestInfo(), "Guest info cannot be null");
        Objects.requireNonNull(command.items(), "Items cannot be null");
        Objects.requireNonNull(command.deliveryAddress(), "Delivery address cannot be null");
        Objects.requireNonNull(command.paymentInfo(), "Payment info cannot be null");
        
        if (command.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        // Validate guest tracking token is not expired
        if (!command.guestInfo().trackingToken().isValid()) {
            throw new IllegalArgumentException("Guest tracking token has expired");
        }
        
        logger.debug("Guest order validation passed for merchant: {}", command.merchantId());
    }
    
    @Override
    public GuestInfo generateGuestInfo(String contactPhone, String contactEmail, String contactName) {
        Objects.requireNonNull(contactPhone, "Contact phone cannot be null");
        Objects.requireNonNull(contactEmail, "Contact email cannot be null");
        Objects.requireNonNull(contactName, "Contact name cannot be null");
        
        logger.debug("Generating guest info for: {}", contactName);
        
        return GuestInfo.create(contactPhone, contactEmail, contactName);
    }
    
    @Override
    public void resendDeliveryCode(GuestTrackingToken token) {
        Objects.requireNonNull(token, "Tracking token cannot be null");
        
        logger.info("Resending delivery code for tracking token: {}", token);
        
        // Find the order
        Order order = findOrderByTrackingToken(token);
        
        // Validate order is in a state where delivery code can be resent
        if (!canResendDeliveryCode(order)) {
            throw new IllegalStateException(
                String.format("Cannot resend delivery code for order in status: %s", order.getStatus()));
        }
        
        // TODO: Integrate with delivery confirmation service to resend code
        // This would typically involve calling the DeliveryConfirmationService
        // For now, we'll log the action and assume the code is resent via notification service
        
        logger.info("Delivery code resent for order: {} to contact: {}", 
                   order.getOrderId(), order.getGuestInfo().contactPhone());
    }
    
    @Override
    public void convertGuestToCustomer(GuestTrackingToken token, String customerId) {
        Objects.requireNonNull(token, "Tracking token cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        
        logger.info("Converting guest order to customer order: {}", customerId);
        
        // Find the order
        Order order = findOrderByTrackingToken(token);
        
        if (!order.isGuestOrder()) {
            throw new IllegalArgumentException("Order is not a guest order");
        }
        
        // Validate the order can be converted
        if (!canConvertToCustomer(order)) {
            throw new IllegalStateException(
                String.format("Cannot convert order in status: %s", order.getStatus()));
        }
        
        // Create new customer order with same details but different customer reference
        // Note: This creates a new order entity rather than modifying the existing one
        // to maintain data integrity and audit trail
        
        com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId customerIdVO = 
            com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId.of(customerId);
        
        Order customerOrder = new Order(
            com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId.generate(),
            order.getMerchantId(),
            customerIdVO,
            order.getItems(),
            order.getDeliveryAddress(),
            order.getPaymentInfo()
        );
        
        // Update the customer order to match the original order's status
        if (order.getStatus() != com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus.PENDING) {
            customerOrder.updateStatus(order.getStatus());
        }
        
        // Save the new customer order
        Order savedCustomerOrder = orderRepository.save(customerOrder);
        
        // Cancel the original guest order with conversion reason
        order.cancel(
            com.xavier.mozdeliveryapi.order.domain.valueobject.CancellationReason.CONVERTED_TO_CUSTOMER,
            "Converted to customer order: " + savedCustomerOrder.getOrderId()
        );
        
        // Update the original order
        orderRepository.save(order);
        
        logger.info("Guest order {} converted to customer order: {}", 
                   order.getOrderId(), savedCustomerOrder.getOrderId());
    }
    
    /**
     * Check if delivery code can be resent for the order.
     */
    private boolean canResendDeliveryCode(Order order) {
        return switch (order.getStatus()) {
            case PAYMENT_CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY -> true;
            default -> false;
        };
    }
    
    /**
     * Check if guest order can be converted to customer order.
     */
    private boolean canConvertToCustomer(Order order) {
        return switch (order.getStatus()) {
            case PENDING, PAYMENT_PROCESSING, PAYMENT_CONFIRMED, PREPARING, 
                 READY_FOR_PICKUP, OUT_FOR_DELIVERY -> true;
            case DELIVERED, CANCELLED, REFUNDED -> false;
        };
    }
}