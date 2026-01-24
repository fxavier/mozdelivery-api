package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderRequest;
import com.xavier.mozdeliveryapi.order.application.dto.GuestOrderResponse;
import com.xavier.mozdeliveryapi.order.application.dto.GuestTrackingResponse;
import com.xavier.mozdeliveryapi.order.application.mapper.OrderMapper;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;

/**
 * Implementation of guest checkout application service.
 */
@Service
@Transactional
public class GuestCheckoutApplicationServiceImpl implements GuestCheckoutApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestCheckoutApplicationServiceImpl.class);
    
    private final GuestCheckoutService guestCheckoutService;
    private final OrderMapper orderMapper;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public GuestCheckoutApplicationServiceImpl(
            GuestCheckoutService guestCheckoutService,
            OrderMapper orderMapper) {
        this.guestCheckoutService = Objects.requireNonNull(guestCheckoutService, "Guest checkout service cannot be null");
        this.orderMapper = Objects.requireNonNull(orderMapper, "Order mapper cannot be null");
    }
    
    @Override
    public GuestOrderResponse createGuestOrder(GuestOrderRequest request) {
        Objects.requireNonNull(request, "Guest order request cannot be null");
        
        logger.info("Creating guest order for tenant: {}", request.tenantId());
        
        // Generate guest info
        GuestInfo guestInfo = guestCheckoutService.generateGuestInfo(
            request.guestInfo().contactPhone(),
            request.guestInfo().contactEmail(),
            request.guestInfo().contactName()
        );
        
        // Map request to domain command
        GuestCheckoutService.GuestOrderCommand command = new GuestCheckoutService.GuestOrderCommand(
            request.tenantId(),
            guestInfo,
            orderMapper.mapOrderItems(request.items(), request.currency()),
            orderMapper.mapDeliveryAddress(request.deliveryAddress()),
            orderMapper.mapPaymentInfo(request.paymentMethod(), request.currency(), 
                                     calculateTotalAmount(request))
        );
        
        // Create the order
        Order order = guestCheckoutService.createGuestOrder(command);
        
        // Convert to response
        GuestOrderResponse response = GuestOrderResponse.from(order, baseUrl);
        
        logger.info("Guest order created successfully: {}", order.getOrderId());
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public GuestTrackingResponse trackGuestOrder(String trackingToken) {
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        
        logger.debug("Tracking guest order with token: {}", trackingToken);
        
        // Parse tracking token
        GuestTrackingToken token = GuestTrackingToken.of(
            trackingToken, 
            java.time.Instant.now().minusSeconds(3600), // Placeholder - would come from token parsing
            java.time.Instant.now().plusSeconds(3600 * 72) // Placeholder
        );
        
        // Find the order
        Order order = guestCheckoutService.findOrderByTrackingToken(token);
        
        // Get merchant name (simplified for now)
        String merchantName = "Merchant " + order.getTenantId().value().toString().substring(0, 8);
        
        // Convert to response
        GuestTrackingResponse response = GuestTrackingResponse.from(order, merchantName);
        
        logger.debug("Guest order tracking retrieved: {}", order.getOrderId());
        
        return response;
    }
    
    @Override
    public void resendDeliveryCode(String trackingToken) {
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        
        logger.info("Resending delivery code for token: {}", trackingToken);
        
        // Parse tracking token
        GuestTrackingToken token = GuestTrackingToken.of(
            trackingToken,
            java.time.Instant.now().minusSeconds(3600), // Placeholder
            java.time.Instant.now().plusSeconds(3600 * 72) // Placeholder
        );
        
        guestCheckoutService.resendDeliveryCode(token);
        
        logger.info("Delivery code resent successfully");
    }
    
    @Override
    public void convertGuestToCustomer(String trackingToken, String customerId) {
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        
        logger.info("Converting guest to customer: {}", customerId);
        
        // Parse tracking token
        GuestTrackingToken token = GuestTrackingToken.of(
            trackingToken,
            java.time.Instant.now().minusSeconds(3600), // Placeholder
            java.time.Instant.now().plusSeconds(3600 * 72) // Placeholder
        );
        
        guestCheckoutService.convertGuestToCustomer(token, customerId);
        
        logger.info("Guest converted to customer successfully");
    }
    
    private com.xavier.mozdeliveryapi.shared.domain.valueobject.Money calculateTotalAmount(GuestOrderRequest request) {
        // Calculate total from items
        java.math.BigDecimal total = request.items().stream()
            .map(item -> item.unitPrice().multiply(java.math.BigDecimal.valueOf(item.quantity())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        return com.xavier.mozdeliveryapi.shared.domain.valueobject.Money.of(total, request.currency());
    }
}