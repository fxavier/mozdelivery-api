package com.xavier.mozdeliveryapi.dispatch.infra.web;

import com.xavier.mozdeliveryapi.dispatch.application.dto.LocationHistoryResponse;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.TrackingApplicationService;
import com.xavier.mozdeliveryapi.dispatch.application.dto.TrackingResponse;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.TimeRange;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;

/**
 * REST controller for delivery tracking operations.
 */
@RestController
@RequestMapping("/api/v1/tracking")
@Tag(name = "Tracking", description = "Real-time delivery tracking operations")
@CrossOrigin(origins = "*")
public class TrackingController {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingController.class);
    
    private final TrackingApplicationService trackingApplicationService;
    
    public TrackingController(TrackingApplicationService trackingApplicationService) {
        this.trackingApplicationService = trackingApplicationService;
    }
    
    @Operation(summary = "Get delivery tracking", description = "Retrieves real-time tracking information for a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<TrackingResponse> getDeliveryTracking(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Getting tracking for delivery: {}", deliveryId);
        
        try {
            Optional<TrackingResponse> tracking = trackingApplicationService
                .getDeliveryTracking(DeliveryId.of(deliveryId));
            
            return tracking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error getting tracking for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get delivery tracking by order", description = "Retrieves real-time tracking information for a delivery by order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order or delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<TrackingResponse> getDeliveryTrackingByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        
        logger.info("Getting tracking for order: {}", orderId);
        
        try {
            Optional<TrackingResponse> tracking = trackingApplicationService
                .getDeliveryTrackingByOrderId(OrderId.of(orderId));
            
            return tracking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error getting tracking for order: {}", orderId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get active deliveries tracking for person", description = "Retrieves tracking information for all active deliveries of a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active deliveries tracking retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/active")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<List<TrackingResponse>> getActiveDeliveryTrackingForPerson(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Getting active delivery tracking for person: {}", deliveryPersonId);
        
        try {
            List<TrackingResponse> activeTracking = trackingApplicationService
                .getActiveDeliveryTrackingForPerson(DeliveryPersonId.of(deliveryPersonId));
            
            return ResponseEntity.ok(activeTracking);
            
        } catch (Exception e) {
            logger.error("Error getting active delivery tracking for person: {}", deliveryPersonId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(summary = "Update delivery location", description = "Updates delivery location with accuracy and speed information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid location data"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/deliveries/{deliveryId}/location")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<TrackingResponse> updateDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "Latitude") @RequestParam double latitude,
            @Parameter(description = "Longitude") @RequestParam double longitude,
            @Parameter(description = "Location accuracy in meters") @RequestParam(defaultValue = "10.0") double accuracy,
            @Parameter(description = "Speed in km/h") @RequestParam(defaultValue = "0.0") double speed) {
        
        logger.info("Updating delivery location: {} to ({}, {}) with accuracy: {} and speed: {}", 
            deliveryId, latitude, longitude, accuracy, speed);
        
        try {
            Location newLocation = Location.of(latitude, longitude);
            TrackingResponse response = trackingApplicationService.updateDeliveryLocation(
                DeliveryId.of(deliveryId), newLocation, accuracy, speed);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating delivery location: {}", deliveryId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Update delivery person location", description = "Updates delivery person location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid location data"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/delivery-persons/{deliveryPersonId}/location")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<Void> updateDeliveryPersonLocation(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId,
            @Parameter(description = "Latitude") @RequestParam double latitude,
            @Parameter(description = "Longitude") @RequestParam double longitude,
            @Parameter(description = "Location accuracy in meters") @RequestParam(defaultValue = "10.0") double accuracy,
            @Parameter(description = "Speed in km/h") @RequestParam(defaultValue = "0.0") double speed) {
        
        logger.info("Updating delivery person location: {} to ({}, {}) with accuracy: {} and speed: {}", 
            deliveryPersonId, latitude, longitude, accuracy, speed);
        
        try {
            Location newLocation = Location.of(latitude, longitude);
            trackingApplicationService.updateDeliveryPersonLocation(
                DeliveryPersonId.of(deliveryPersonId), newLocation, accuracy, speed);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error updating delivery person location: {}", deliveryPersonId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Get delivery person current location", description = "Retrieves current location of a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current location retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found or location not available"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/location")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Location> getDeliveryPersonCurrentLocation(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Getting current location for delivery person: {}", deliveryPersonId);
        
        try {
            Optional<Location> location = trackingApplicationService
                .getDeliveryPersonCurrentLocation(DeliveryPersonId.of(deliveryPersonId));
            
            return location.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error getting current location for delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get delivery person location history", description = "Retrieves location history for a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location history retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/location/history")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<List<LocationHistoryResponse>> getDeliveryPersonLocationHistory(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        logger.info("Getting location history for delivery person: {} from {} to {}", 
            deliveryPersonId, startTime, endTime);
        
        try {
            TimeRange timeRange = new TimeRange(startTime, endTime);
            List<LocationHistoryResponse> history = trackingApplicationService
                .getDeliveryPersonLocationHistory(DeliveryPersonId.of(deliveryPersonId), timeRange);
            
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error getting location history for delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Get recent location history", description = "Retrieves recent location history for a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent location history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/location/recent")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<List<LocationHistoryResponse>> getDeliveryPersonRecentLocationHistory(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId,
            @Parameter(description = "Maximum number of records") @RequestParam(defaultValue = "50") int limit) {
        
        logger.info("Getting recent location history for delivery person: {} with limit: {}", 
            deliveryPersonId, limit);
        
        try {
            List<LocationHistoryResponse> recentHistory = trackingApplicationService
                .getDeliveryPersonRecentLocationHistory(DeliveryPersonId.of(deliveryPersonId), limit);
            
            return ResponseEntity.ok(recentHistory);
            
        } catch (Exception e) {
            logger.error("Error getting recent location history for delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(summary = "Calculate delivery ETA", description = "Calculates estimated arrival time for a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "ETA calculated successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}/eta")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Instant> calculateDeliveryEstimatedArrival(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Calculating ETA for delivery: {}", deliveryId);
        
        try {
            Optional<Instant> eta = trackingApplicationService
                .calculateDeliveryEstimatedArrival(DeliveryId.of(deliveryId));
            
            return eta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error calculating ETA for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get delivery progress", description = "Gets delivery progress as percentage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progress retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}/progress")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Double> getDeliveryProgress(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Getting progress for delivery: {}", deliveryId);
        
        try {
            Optional<Double> progress = trackingApplicationService
                .getDeliveryProgress(DeliveryId.of(deliveryId));
            
            return progress.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error getting progress for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get time to delivery", description = "Gets time remaining until delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Time to delivery retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}/time-remaining")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Duration> getTimeToDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Getting time remaining for delivery: {}", deliveryId);
        
        try {
            Optional<Duration> timeRemaining = trackingApplicationService
                .getTimeToDelivery(DeliveryId.of(deliveryId));
            
            return timeRemaining.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error getting time remaining for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Check if delivery is overdue", description = "Checks if delivery is overdue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}/overdue")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Boolean> isDeliveryOverdue(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Checking if delivery is overdue: {}", deliveryId);
        
        try {
            boolean isOverdue = trackingApplicationService.isDeliveryOverdue(DeliveryId.of(deliveryId));
            return ResponseEntity.ok(isOverdue);
            
        } catch (Exception e) {
            logger.error("Error checking if delivery is overdue: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get overdue deliveries tracking", description = "Retrieves tracking information for all overdue deliveries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue deliveries tracking retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/overdue")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<List<TrackingResponse>> getOverdueDeliveryTracking() {
        
        logger.info("Getting overdue deliveries tracking");
        
        try {
            List<TrackingResponse> overdueTracking = trackingApplicationService.getOverdueDeliveryTracking();
            return ResponseEntity.ok(overdueTracking);
            
        } catch (Exception e) {
            logger.error("Error getting overdue deliveries tracking", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(summary = "Start delivery tracking", description = "Starts real-time tracking for a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery tracking started successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/deliveries/{deliveryId}/start")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<Void> startDeliveryTracking(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Starting tracking for delivery: {}", deliveryId);
        
        try {
            trackingApplicationService.startDeliveryTracking(DeliveryId.of(deliveryId));
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error starting tracking for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Stop delivery tracking", description = "Stops real-time tracking for a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery tracking stopped successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/deliveries/{deliveryId}/stop")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<Void> stopDeliveryTracking(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Stopping tracking for delivery: {}", deliveryId);
        
        try {
            trackingApplicationService.stopDeliveryTracking(DeliveryId.of(deliveryId));
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error stopping tracking for delivery: {}", deliveryId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Start delivery person tracking", description = "Starts location tracking for a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person tracking started successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/delivery-persons/{deliveryPersonId}/start")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<Void> startDeliveryPersonTracking(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Starting tracking for delivery person: {}", deliveryPersonId);
        
        try {
            trackingApplicationService.startDeliveryPersonTracking(DeliveryPersonId.of(deliveryPersonId));
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error starting tracking for delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Stop delivery person tracking", description = "Stops location tracking for a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person tracking stopped successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/delivery-persons/{deliveryPersonId}/stop")
    @PreAuthorize("hasAuthority('SCOPE_tracking:write')")
    public ResponseEntity<Void> stopDeliveryPersonTracking(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Stopping tracking for delivery person: {}", deliveryPersonId);
        
        try {
            trackingApplicationService.stopDeliveryPersonTracking(DeliveryPersonId.of(deliveryPersonId));
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error stopping tracking for delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Check if delivery person is tracked", description = "Checks if delivery person location is being tracked")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tracking status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/tracked")
    @PreAuthorize("hasAuthority('SCOPE_tracking:read')")
    public ResponseEntity<Boolean> isDeliveryPersonTracked(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Checking if delivery person is tracked: {}", deliveryPersonId);
        
        try {
            boolean isTracked = trackingApplicationService.isDeliveryPersonTracked(
                DeliveryPersonId.of(deliveryPersonId));
            
            return ResponseEntity.ok(isTracked);
            
        } catch (Exception e) {
            logger.error("Error checking if delivery person is tracked: {}", deliveryPersonId, e);
            return ResponseEntity.notFound().build();
        }
    }
}