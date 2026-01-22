package com.xavier.mozdeliveryapi.order.infrastructure.web;

import com.xavier.mozdeliveryapi.order.application.*;
import com.xavier.mozdeliveryapi.order.domain.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.OrderFilter;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.OrderStatus;
import com.xavier.mozdeliveryapi.shared.infrastructure.multitenant.TenantContext;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for order management operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management operations")
@CrossOrigin(origins = "*")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderApplicationService orderApplicationService;
    
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }
    
    @Operation(summary = "Create a new order", description = "Creates a new order for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_order:write')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        logger.info("Creating order for tenant: {}", TenantContext.getCurrentTenant());
        
        try {
            OrderResponse response = orderApplicationService.createOrder(request);
            logger.info("Order created successfully with ID: {}", response.orderId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_order:read')")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        
        logger.info("Getting order: {}", orderId);
        
        try {
            OrderResponse response = orderApplicationService.getOrder(OrderId.of(orderId));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get orders for tenant", description = "Retrieves all orders for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_order:read')")
    public ResponseEntity<List<OrderResponse>> getOrdersForTenant(
            @Parameter(description = "Order status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Customer ID filter") @RequestParam(required = false) String customerId) {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting orders for tenant: {}", tenantId);
        
        try {
            OrderFilter filter = new OrderFilter(
                status != null ? Optional.of(OrderStatus.valueOf(status)) : Optional.empty(),
                customerId != null ? Optional.of(CustomerId.of(customerId)) : Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0,
                20
            );
            
            List<OrderResponse> orders = orderApplicationService.getOrdersForTenant(
                TenantId.of(tenantId), filter);
            
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            logger.error("Error getting orders for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('SCOPE_order:write')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Parameter(description = "New status") @RequestParam String status) {
        
        logger.info("Updating order status: {} to {}", orderId, status);
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            OrderResponse response = orderApplicationService.updateOrderStatus(
                OrderId.of(orderId), orderStatus);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {}", status, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating order status: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Cancel order", description = "Cancels an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid cancellation request"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_order:write')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody CancellationRequest request) {
        
        logger.info("Cancelling order: {}", orderId);
        
        try {
            OrderResponse response = orderApplicationService.cancelOrder(
                OrderId.of(orderId), request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Confirm payment", description = "Confirms payment for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment confirmation"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/{orderId}/payment/confirm")
    @PreAuthorize("hasAuthority('SCOPE_order:write')")
    public ResponseEntity<OrderResponse> confirmPayment(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody PaymentConfirmationRequest request) {
        
        logger.info("Confirming payment for order: {}", orderId);
        
        try {
            OrderResponse response = orderApplicationService.confirmPayment(
                OrderId.of(orderId), request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error confirming payment for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Fail payment", description = "Marks payment as failed for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment failure processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment failure request"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/{orderId}/payment/fail")
    @PreAuthorize("hasAuthority('SCOPE_order:write')")
    public ResponseEntity<OrderResponse> failPayment(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody PaymentFailureRequest request) {
        
        logger.info("Processing payment failure for order: {}", orderId);
        
        try {
            OrderResponse response = orderApplicationService.failPayment(
                OrderId.of(orderId), request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing payment failure for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get order statistics", description = "Retrieves order statistics for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('SCOPE_order:read')")
    public ResponseEntity<OrderStatistics> getOrderStatistics() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting order statistics for tenant: {}", tenantId);
        
        try {
            OrderStatistics statistics = orderApplicationService.getOrderStatistics(
                TenantId.of(tenantId));
            
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error getting order statistics for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}