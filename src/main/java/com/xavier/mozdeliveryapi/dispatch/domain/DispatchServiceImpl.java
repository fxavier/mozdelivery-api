package com.xavier.mozdeliveryapi.dispatch.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.Route;
import com.xavier.mozdeliveryapi.geospatial.domain.RouteOptimizer;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Implementation of dispatch service.
 */
public class DispatchServiceImpl implements DispatchService {
    
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryAssignmentService assignmentService;
    private final RouteOptimizer routeOptimizer;
    
    public DispatchServiceImpl(DeliveryRepository deliveryRepository,
                              DeliveryPersonRepository deliveryPersonRepository,
                              DeliveryAssignmentService assignmentService,
                              RouteOptimizer routeOptimizer) {
        this.deliveryRepository = Objects.requireNonNull(deliveryRepository, 
                                                        "Delivery repository cannot be null");
        this.deliveryPersonRepository = Objects.requireNonNull(deliveryPersonRepository, 
                                                              "Delivery person repository cannot be null");
        this.assignmentService = Objects.requireNonNull(assignmentService, 
                                                       "Assignment service cannot be null");
        this.routeOptimizer = Objects.requireNonNull(routeOptimizer, 
                                                    "Route optimizer cannot be null");
    }
    
    @Override
    public Delivery assignDelivery(TenantId tenantId, OrderId orderId, Location pickupLocation,
                                  Location deliveryLocation, int orderWeight, int orderVolume) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(pickupLocation, "Pickup location cannot be null");
        Objects.requireNonNull(deliveryLocation, "Delivery location cannot be null");
        
        // Find the best delivery person
        DeliveryAssignment assignment = assignmentService
            .findBestDeliveryPerson(tenantId, orderId, pickupLocation, deliveryLocation, orderWeight, orderVolume)
            .orElseThrow(() -> new DeliveryAssignmentException("No available delivery person found for order: " + orderId));
        
        // Get the delivery person and assign the delivery
        DeliveryPerson deliveryPerson = deliveryPersonRepository
            .findById(assignment.deliveryPersonId())
            .orElseThrow(() -> new DeliveryAssignmentException("Delivery person not found: " + assignment.deliveryPersonId()));
        
        // Create route from pickup to delivery location
        Route route = routeOptimizer.optimizeRoute(
            deliveryPerson.getCurrentLocation(),
            List.of(pickupLocation),
            deliveryLocation
        );
        
        // Create delivery
        Delivery delivery = new Delivery(
            DeliveryId.generate(),
            tenantId,
            orderId,
            deliveryPerson.getDeliveryPersonId(),
            route,
            orderWeight,
            orderVolume
        );
        
        // Assign delivery to person (updates capacity)
        deliveryPerson.assignDelivery(orderWeight, orderVolume);
        
        // Save both entities
        deliveryPersonRepository.save(deliveryPerson);
        return deliveryRepository.save(delivery);
    }
    
    @Override
    public Delivery reassignDelivery(DeliveryId deliveryId, DeliveryPersonId newDeliveryPersonId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newDeliveryPersonId, "New delivery person ID cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        DeliveryPerson oldDeliveryPerson = deliveryPersonRepository
            .findById(delivery.getDeliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Old delivery person not found: " + delivery.getDeliveryPersonId()));
        
        DeliveryPerson newDeliveryPerson = deliveryPersonRepository
            .findById(newDeliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("New delivery person not found: " + newDeliveryPersonId));
        
        // Check if new delivery person can handle the order
        if (!newDeliveryPerson.canAcceptDelivery(delivery.getOrderWeight(), delivery.getOrderVolume())) {
            throw new DeliveryAssignmentException("New delivery person cannot handle the order capacity");
        }
        
        // Create new route from new delivery person's location
        Route newRoute = routeOptimizer.optimizeRoute(
            newDeliveryPerson.getCurrentLocation(),
            delivery.getRoute().getLocations().subList(1, delivery.getRoute().getLocations().size() - 1),
            delivery.getRoute().getEndLocation()
        );
        
        // Update capacities
        oldDeliveryPerson.completeDelivery(delivery.getOrderWeight(), delivery.getOrderVolume());
        newDeliveryPerson.assignDelivery(delivery.getOrderWeight(), delivery.getOrderVolume());
        
        // Reassign delivery
        delivery.reassign(newDeliveryPersonId, newRoute);
        
        // Save all entities
        deliveryPersonRepository.save(oldDeliveryPerson);
        deliveryPersonRepository.save(newDeliveryPerson);
        return deliveryRepository.save(delivery);
    }
    
    @Override
    public Delivery updateDeliveryStatus(DeliveryId deliveryId, DeliveryStatus newStatus, String notes) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        delivery.updateStatus(newStatus, notes);
        
        // If delivery is completed, free up delivery person capacity
        if (newStatus.isCompleted()) {
            DeliveryPerson deliveryPerson = deliveryPersonRepository
                .findById(delivery.getDeliveryPersonId())
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + delivery.getDeliveryPersonId()));
            
            deliveryPerson.completeDelivery(delivery.getOrderWeight(), delivery.getOrderVolume());
            deliveryPersonRepository.save(deliveryPerson);
        }
        
        return deliveryRepository.save(delivery);
    }
    
    @Override
    public Delivery updateDeliveryStatus(DeliveryId deliveryId, DeliveryStatus newStatus) {
        return updateDeliveryStatus(deliveryId, newStatus, null);
    }
    
    @Override
    public Delivery updateDeliveryLocation(DeliveryId deliveryId, Location newLocation) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        delivery.updateLocation(newLocation);
        
        // Also update delivery person location
        DeliveryPerson deliveryPerson = deliveryPersonRepository
            .findById(delivery.getDeliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + delivery.getDeliveryPersonId()));
        
        deliveryPerson.updateLocation(newLocation);
        deliveryPersonRepository.save(deliveryPerson);
        
        return deliveryRepository.save(delivery);
    }
    
    @Override
    public Delivery cancelDelivery(DeliveryId deliveryId, String reason) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        delivery.cancel(reason);
        
        // Free up delivery person capacity
        DeliveryPerson deliveryPerson = deliveryPersonRepository
            .findById(delivery.getDeliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + delivery.getDeliveryPersonId()));
        
        deliveryPerson.completeDelivery(delivery.getOrderWeight(), delivery.getOrderVolume());
        deliveryPersonRepository.save(deliveryPerson);
        
        return deliveryRepository.save(delivery);
    }
    
    @Override
    public List<Delivery> getActiveDeliveriesForPerson(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        return deliveryRepository.findActiveByDeliveryPersonId(deliveryPersonId);
    }
    
    @Override
    public List<Delivery> getOverdueDeliveries() {
        return deliveryRepository.findOverdueDeliveries(Instant.now());
    }
    
    @Override
    public Route optimizeRoute(DeliveryPersonId deliveryPersonId, List<DeliveryId> deliveryIds) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(deliveryIds, "Delivery IDs cannot be null");
        
        if (deliveryIds.isEmpty()) {
            throw new IllegalArgumentException("Delivery IDs list cannot be empty");
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository
            .findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + deliveryPersonId));
        
        // Get all delivery locations
        List<Location> locations = deliveryIds.stream()
            .map(deliveryId -> deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId)))
            .flatMap(delivery -> delivery.getRoute().getLocations().stream())
            .distinct()
            .toList();
        
        return routeOptimizer.optimizeRoute(
            deliveryPerson.getCurrentLocation(),
            locations.subList(1, locations.size() - 1),
            locations.get(locations.size() - 1)
        );
    }
}