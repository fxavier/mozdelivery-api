package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.DeliveryPerson;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryAssignment;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.DeliveryPersonRepository;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.DeliveryRepository;

/**
 * Implementation of delivery assignment service with load balancing and optimization logic.
 */
public class DeliveryAssignmentServiceImpl implements DeliveryAssignmentService {
    
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryRepository deliveryRepository;
    
    // Maximum distance for delivery assignment (50km)
    private static final Distance MAX_ASSIGNMENT_DISTANCE = Distance.ofKilometers(50);
    
    public DeliveryAssignmentServiceImpl(DeliveryPersonRepository deliveryPersonRepository,
                                        DeliveryRepository deliveryRepository) {
        this.deliveryPersonRepository = Objects.requireNonNull(deliveryPersonRepository, 
                                                              "Delivery person repository cannot be null");
        this.deliveryRepository = Objects.requireNonNull(deliveryRepository, 
                                                        "Delivery repository cannot be null");
    }
    
    @Override
    public Optional<DeliveryAssignment> findBestDeliveryPerson(TenantId tenantId, OrderId orderId,
                                                              Location pickupLocation, Location deliveryLocation,
                                                              int orderWeight, int orderVolume) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(pickupLocation, "Pickup location cannot be null");
        Objects.requireNonNull(deliveryLocation, "Delivery location cannot be null");
        
        List<DeliveryAssignment> assignments = getAllPossibleAssignments(
            tenantId, orderId, pickupLocation, deliveryLocation, orderWeight, orderVolume);
        
        return assignments.isEmpty() ? Optional.empty() : Optional.of(assignments.get(0));
    }
    
    @Override
    public List<DeliveryAssignment> getAllPossibleAssignments(TenantId tenantId, OrderId orderId,
                                                              Location pickupLocation, Location deliveryLocation,
                                                              int orderWeight, int orderVolume) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(pickupLocation, "Pickup location cannot be null");
        Objects.requireNonNull(deliveryLocation, "Delivery location cannot be null");
        
        // Find available delivery persons within reasonable distance
        List<DeliveryPerson> availablePersons = deliveryPersonRepository
            .findAvailableWithinDistance(tenantId, pickupLocation, MAX_ASSIGNMENT_DISTANCE);
        
        int orderPriority = calculateOrderPriority(orderId, pickupLocation, deliveryLocation);
        
        return availablePersons.stream()
            .filter(person -> person.canAcceptDelivery(orderWeight, orderVolume))
            .map(person -> createDeliveryAssignment(person, pickupLocation, orderPriority))
            .sorted(Comparator.comparingDouble(DeliveryAssignment::getAssignmentScore).reversed())
            .toList();
    }
    
    @Override
    public boolean canDeliveryPersonHandleOrder(DeliveryPersonId deliveryPersonId, 
                                               int orderWeight, int orderVolume) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return deliveryPersonRepository.findById(deliveryPersonId)
            .map(person -> person.canAcceptDelivery(orderWeight, orderVolume))
            .orElse(false);
    }
    
    @Override
    public int calculateOrderPriority(OrderId orderId, Location pickupLocation, Location deliveryLocation) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(pickupLocation, "Pickup location cannot be null");
        Objects.requireNonNull(deliveryLocation, "Delivery location cannot be null");
        
        // Base priority
        int priority = 5;
        
        // Increase priority for shorter distances (faster delivery)
        Distance deliveryDistance = pickupLocation.distanceTo(deliveryLocation);
        if (deliveryDistance.getKilometers().doubleValue() < 5.0) {
            priority += 2; // Short distance orders get higher priority
        } else if (deliveryDistance.getKilometers().doubleValue() > 20.0) {
            priority -= 1; // Long distance orders get lower priority
        }
        
        // TODO: Add more priority factors:
        // - Order value (higher value = higher priority)
        // - Customer tier (premium customers = higher priority)
        // - Order age (older orders = higher priority)
        // - Special requirements (pharmacy, time-sensitive = higher priority)
        
        return Math.max(1, Math.min(10, priority)); // Clamp between 1 and 10
    }
    
    private DeliveryAssignment createDeliveryAssignment(DeliveryPerson person, 
                                                       Location pickupLocation, 
                                                       int priority) {
        Distance distanceToPickup = person.getCurrentLocation().distanceTo(pickupLocation);
        double capacityUtilization = person.getCapacityUtilization();
        
        return DeliveryAssignment.of(
            person.getDeliveryPersonId(),
            distanceToPickup,
            capacityUtilization,
            priority
        );
    }
}
