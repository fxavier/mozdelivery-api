package com.xavier.mozdeliveryapi.notification.infra.web;

import com.xavier.mozdeliveryapi.notification.application.usecase.NotificationApplicationService;
import com.xavier.mozdeliveryapi.notification.application.dto.NotificationResponse;
import com.xavier.mozdeliveryapi.notification.application.dto.SendNotificationRequest;
import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
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
import com.xavier.mozdeliveryapi.notification.domain.entity.Notification;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management operations")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationApplicationService notificationApplicationService;
    
    public NotificationController(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }
    
    @Operation(summary = "Send notification", description = "Sends a notification to recipients")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid notification request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_notification:write')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        
        logger.info("Sending notification for merchant: {} to recipient: {}", 
            TenantContext.getCurrentTenant(), request.recipient());
        
        try {
            NotificationResponse response = notificationApplicationService.sendNotification(request);
            logger.info("Notification sent successfully with ID: {}", response.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error sending notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get notification", description = "Retrieves a notification by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification found"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAuthority('SCOPE_notification:read')")
    public ResponseEntity<NotificationResponse> getNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        
        logger.info("Getting notification: {}", notificationId);
        
        try {
            NotificationResponse response = notificationApplicationService.getNotification(notificationId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting notification: {}", notificationId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get notifications for merchant", description = "Retrieves all notifications for the authenticated merchant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_notification:read')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForMerchant() {
        
        String merchantId = TenantContext.getCurrentTenant();
        logger.info("Getting notifications for merchant: {}", merchantId);
        
        try {
            List<NotificationResponse> notifications = notificationApplicationService
                .getNotificationsForMerchant(merchantId);
            
            return ResponseEntity.ok(notifications);
            
        } catch (Exception e) {
            logger.error("Error getting notifications for merchant: {}", merchantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Cancel notification", description = "Cancels a pending notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "400", description = "Notification cannot be cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAuthority('SCOPE_notification:write')")
    public ResponseEntity<Void> cancelNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        
        logger.info("Cancelling notification: {}", notificationId);
        
        try {
            notificationApplicationService.cancelNotification(notificationId);
            logger.info("Notification cancelled successfully: {}", notificationId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error cancelling notification: {}", notificationId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Process pending notifications", description = "Processes all pending notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending notifications processed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/process-pending")
    @PreAuthorize("hasAuthority('SCOPE_notification:admin')")
    public ResponseEntity<Integer> processPendingNotifications() {
        
        logger.info("Processing pending notifications");
        
        try {
            int processedCount = notificationApplicationService.processPendingNotifications();
            logger.info("Processed {} pending notifications", processedCount);
            return ResponseEntity.ok(processedCount);
            
        } catch (Exception e) {
            logger.error("Error processing pending notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
