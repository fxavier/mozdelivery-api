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
        
        logger.info("Creating guest order for merchant: {}", request.merchantId());
        
        // Generate guest info
        GuestInfo guestInfo = guestCheckoutService.generateGuestInfo(
            request.guestInfo().contactPhone(),
            request.guestInfo().contactEmail(),
            request.guestInfo().contactName()
        );
        
        // Map request to domain command
        GuestCheckoutService.GuestOrderCommand command = new GuestCheckoutService.GuestOrderCommand(
            request.merchantId(),
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
        
        // Parse and validate tracking token
        GuestTrackingToken token = parseTrackingToken(trackingToken);
        
        // Find the order
        Order order = guestCheckoutService.findOrderByTrackingToken(token);
        
        // Get merchant name (simplified for now - in real implementation would use merchant service)
        String merchantName = getMerchantName(order.getMerchantId());
        
        // Convert to response
        GuestTrackingResponse response = GuestTrackingResponse.from(order, merchantName);
        
        logger.debug("Guest order tracking retrieved: {}", order.getOrderId());
        
        return response;
    }
    
    @Override
    public void resendDeliveryCode(String trackingToken) {
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        
        logger.info("Resending delivery code for token: {}", trackingToken);
        
        // Parse and validate tracking token
        GuestTrackingToken token = parseTrackingToken(trackingToken);
        
        guestCheckoutService.resendDeliveryCode(token);
        
        logger.info("Delivery code resent successfully");
    }
    
    @Override
    public void convertGuestToCustomer(String trackingToken, String customerId) {
        Objects.requireNonNull(trackingToken, "Tracking token cannot be null");
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        
        logger.info("Converting guest to customer: {}", customerId);
        
        // Parse and validate tracking token
        GuestTrackingToken token = parseTrackingToken(trackingToken);
        
        guestCheckoutService.convertGuestToCustomer(token, customerId);
        
        logger.info("Guest converted to customer successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public GuestTrackingResponse getOrderStatusUpdates(String trackingToken) {
        // This method provides the same functionality as trackGuestOrder
        // but is semantically different - it's specifically for status updates
        return trackGuestOrder(trackingToken);
    }
    
    /**
     * Parse tracking token from string format.
     * In a real implementation, this would decode a structured token containing
     * the token value, generation time, and expiry time.
     */
    private GuestTrackingToken parseTrackingToken(String tokenString) {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking token cannot be null or empty");
        }
        
        try {
            // For now, we'll use a simple approach where the token string is the actual token
            // In a real implementation, this might be a JWT or encrypted token containing metadata
            
            // Generate reasonable defaults for parsing
            java.time.Instant now = java.time.Instant.now();
            java.time.Instant generatedAt = now.minusSeconds(3600); // Assume generated 1 hour ago
            java.time.Instant expiresAt = now.plusSeconds(3600 * 71); // Expires in 71 hours from now
            
            return GuestTrackingToken.of(tokenString.trim(), generatedAt, expiresAt);
            
        } catch (Exception e) {
            logger.warn("Failed to parse tracking token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid tracking token format", e);
        }
    }
    
    /**
     * Get merchant name for display.
     * In a real implementation, this would call the merchant service.
     */
    private String getMerchantName(com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId merchantId) {
        // Simplified implementation - in real system would call merchant service
        return "Merchant " + merchantId.value().toString().substring(0, 8);
    }
    
    private com.xavier.mozdeliveryapi.shared.domain.valueobject.Money calculateTotalAmount(GuestOrderRequest request) {
        // Calculate total from items
        java.math.BigDecimal total = request.items().stream()
            .map(item -> item.unitPrice().multiply(java.math.BigDecimal.valueOf(item.quantity())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        return com.xavier.mozdeliveryapi.shared.domain.valueobject.Money.of(total, request.currency());
    }
}