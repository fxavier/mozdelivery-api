package com.xavier.mozdeliveryapi.dispatch.infra.web;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
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
import com.xavier.mozdeliveryapi.dispatch.application.dto.AssignDeliveryRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CreateDeliveryPersonRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.DeliveryPersonResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.DeliveryResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateDeliveryStatusRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateLocationRequest;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.DispatchApplicationService;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;

/**
 * REST controller for dispatch operations.
 */
@RestController
@RequestMapping("/api/v1/dispatch")
@Tag(name = "Dispatch", description = "Delivery dispatch and management operations")
@CrossOrigin(origins = "*")
public class DispatchController {
    
    private static final Logger logger = LoggerFactory.getLogger(DispatchController.class);
    
    private final DispatchApplicationService dispatchApplicationService;
    
    public DispatchController(DispatchApplicationService dispatchApplicationService) {
        this.dispatchApplicationService = dispatchApplicationService;
    }
    
    @Operation(summary = "Create delivery person", description = "Creates a new delivery person for the tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Delivery person created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/delivery-persons")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryPersonResponse> createDeliveryPerson(
            @Valid @RequestBody CreateDeliveryPersonRequest request) {
        
        logger.info("Creating delivery person for tenant: {}", TenantContext.getCurrentTenant());
        
        try {
            DeliveryPersonResponse response = dispatchApplicationService.createDeliveryPerson(request);
            logger.info("Delivery person created successfully with ID: {}", response.deliveryPersonId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating delivery person", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Assign delivery", description = "Assigns a delivery for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Delivery assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assignment request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/deliveries")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryResponse> assignDelivery(
            @Valid @RequestBody AssignDeliveryRequest request) {
        
        logger.info("Assigning delivery for order: {}", request.orderId());
        
        try {
            DeliveryResponse response = dispatchApplicationService.assignDelivery(request);
            logger.info("Delivery assigned successfully with ID: {}", response.deliveryId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error assigning delivery for order: {}", request.orderId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get delivery", description = "Retrieves a delivery by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/{deliveryId}")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<DeliveryResponse> getDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId) {
        
        logger.info("Getting delivery: {}", deliveryId);
        
        try {
            DeliveryResponse response = dispatchApplicationService.getDelivery(DeliveryId.of(deliveryId));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting delivery: {}", deliveryId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Update delivery status", description = "Updates the status of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status update"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/deliveries/{deliveryId}/status")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {
        
        logger.info("Updating delivery status: {} to {}", deliveryId, request.newStatus());
        
        try {
            DeliveryResponse response = dispatchApplicationService.updateDeliveryStatus(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating delivery status: {}", deliveryId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Update delivery location", description = "Updates the location of a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid location update"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/deliveries/{deliveryId}/location")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryResponse> updateDeliveryLocation(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Valid @RequestBody UpdateLocationRequest request) {
        
        logger.info("Updating delivery location: {}", deliveryId);
        
        try {
            DeliveryResponse response = dispatchApplicationService.updateDeliveryLocation(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating delivery location: {}", deliveryId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Cancel delivery", description = "Cancels a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid cancellation"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/deliveries/{deliveryId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryResponse> cancelDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        
        logger.info("Cancelling delivery: {} with reason: {}", deliveryId, reason);
        
        try {
            DeliveryResponse response = dispatchApplicationService.cancelDelivery(
                DeliveryId.of(deliveryId), reason);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling delivery: {}", deliveryId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Reassign delivery", description = "Reassigns a delivery to a different delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery reassigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid reassignment"),
        @ApiResponse(responseCode = "404", description = "Delivery not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/deliveries/{deliveryId}/reassign")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryResponse> reassignDelivery(
            @Parameter(description = "Delivery ID") @PathVariable String deliveryId,
            @Parameter(description = "New delivery person ID") @RequestParam String newDeliveryPersonId) {
        
        logger.info("Reassigning delivery: {} to delivery person: {}", deliveryId, newDeliveryPersonId);
        
        try {
            DeliveryResponse response = dispatchApplicationService.reassignDelivery(
                DeliveryId.of(deliveryId), DeliveryPersonId.of(newDeliveryPersonId));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error reassigning delivery: {}", deliveryId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Get delivery person", description = "Retrieves a delivery person by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person found"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<DeliveryPersonResponse> getDeliveryPerson(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Getting delivery person: {}", deliveryPersonId);
        
        try {
            DeliveryPersonResponse response = dispatchApplicationService.getDeliveryPerson(
                DeliveryPersonId.of(deliveryPersonId));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting delivery person: {}", deliveryPersonId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get delivery persons for tenant", description = "Retrieves all delivery persons for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery persons retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<List<DeliveryPersonResponse>> getDeliveryPersonsForTenant() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting delivery persons for tenant: {}", tenantId);
        
        try {
            List<DeliveryPersonResponse> deliveryPersons = dispatchApplicationService
                .getDeliveryPersonsForTenant(TenantId.of(tenantId));
            
            return ResponseEntity.ok(deliveryPersons);
            
        } catch (Exception e) {
            logger.error("Error getting delivery persons for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get available delivery persons", description = "Retrieves available delivery persons for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available delivery persons retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/available")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<List<DeliveryPersonResponse>> getAvailableDeliveryPersonsForTenant() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting available delivery persons for tenant: {}", tenantId);
        
        try {
            List<DeliveryPersonResponse> availablePersons = dispatchApplicationService
                .getAvailableDeliveryPersonsForTenant(TenantId.of(tenantId));
            
            return ResponseEntity.ok(availablePersons);
            
        } catch (Exception e) {
            logger.error("Error getting available delivery persons for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get deliveries for tenant", description = "Retrieves all deliveries for the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesForTenant() {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting deliveries for tenant: {}", tenantId);
        
        try {
            List<DeliveryResponse> deliveries = dispatchApplicationService
                .getDeliveriesForTenant(TenantId.of(tenantId));
            
            return ResponseEntity.ok(deliveries);
            
        } catch (Exception e) {
            logger.error("Error getting deliveries for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get active deliveries for person", description = "Retrieves active deliveries for a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active deliveries retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/delivery-persons/{deliveryPersonId}/deliveries/active")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<List<DeliveryResponse>> getActiveDeliveriesForPerson(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId) {
        
        logger.info("Getting active deliveries for delivery person: {}", deliveryPersonId);
        
        try {
            List<DeliveryResponse> activeDeliveries = dispatchApplicationService
                .getActiveDeliveriesForPerson(DeliveryPersonId.of(deliveryPersonId));
            
            return ResponseEntity.ok(activeDeliveries);
            
        } catch (Exception e) {
            logger.error("Error getting active deliveries for person: {}", deliveryPersonId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get overdue deliveries", description = "Retrieves all overdue deliveries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue deliveries retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/deliveries/overdue")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:read')")
    public ResponseEntity<List<DeliveryResponse>> getOverdueDeliveries() {
        
        logger.info("Getting overdue deliveries");
        
        try {
            List<DeliveryResponse> overdueDeliveries = dispatchApplicationService.getOverdueDeliveries();
            return ResponseEntity.ok(overdueDeliveries);
            
        } catch (Exception e) {
            logger.error("Error getting overdue deliveries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Update delivery person status", description = "Updates the status of a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/delivery-persons/{deliveryPersonId}/status")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryPersonResponse> updateDeliveryPersonStatus(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId,
            @Parameter(description = "New status") @RequestParam String status) {
        
        logger.info("Updating delivery person status: {} to {}", deliveryPersonId, status);
        
        try {
            DeliveryPersonStatus newStatus = DeliveryPersonStatus.valueOf(status);
            DeliveryPersonResponse response = dispatchApplicationService.updateDeliveryPersonStatus(
                DeliveryPersonId.of(deliveryPersonId), newStatus);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid delivery person status: {}", status, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating delivery person status: {}", deliveryPersonId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Update delivery person location", description = "Updates the location of a delivery person")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery person location updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid location"),
        @ApiResponse(responseCode = "404", description = "Delivery person not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/delivery-persons/{deliveryPersonId}/location")
    @PreAuthorize("hasAuthority('SCOPE_dispatch:write')")
    public ResponseEntity<DeliveryPersonResponse> updateDeliveryPersonLocation(
            @Parameter(description = "Delivery person ID") @PathVariable String deliveryPersonId,
            @Parameter(description = "Latitude") @RequestParam double latitude,
            @Parameter(description = "Longitude") @RequestParam double longitude) {
        
        logger.info("Updating delivery person location: {} to ({}, {})", deliveryPersonId, latitude, longitude);
        
        try {
            Location newLocation = Location.of(latitude, longitude);
            DeliveryPersonResponse response = dispatchApplicationService.updateDeliveryPersonLocation(
                DeliveryPersonId.of(deliveryPersonId), newLocation);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating delivery person location: {}", deliveryPersonId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}