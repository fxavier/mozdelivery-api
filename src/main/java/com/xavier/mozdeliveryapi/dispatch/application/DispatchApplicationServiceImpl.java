package com.xavier.mozdeliveryapi.dispatch.application;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.dispatch.domain.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryNotFoundException;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPerson;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.DeliveryRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.DispatchService;
import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Implementation of dispatch application service.
 */
@Service
@Transactional
public class DispatchApplicationServiceImpl implements DispatchApplicationService {
    
    private final DispatchService dispatchService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    
    public DispatchApplicationServiceImpl(DispatchService dispatchService,
                                         DeliveryRepository deliveryRepository,
                                         DeliveryPersonRepository deliveryPersonRepository) {
        this.dispatchService = Objects.requireNonNull(dispatchService, "Dispatch service cannot be null");
        this.deliveryRepository = Objects.requireNonNull(deliveryRepository, "Delivery repository cannot be null");
        this.deliveryPersonRepository = Objects.requireNonNull(deliveryPersonRepository, 
                                                              "Delivery person repository cannot be null");
    }
    
    @Override
    public DeliveryPersonResponse createDeliveryPerson(CreateDeliveryPersonRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        // Check if phone number is already in use
        deliveryPersonRepository.findByPhoneNumber(request.phoneNumber())
            .ifPresent(existing -> {
                throw new IllegalArgumentException("Phone number already in use: " + request.phoneNumber());
            });
        
        DeliveryPerson deliveryPerson = new DeliveryPerson(
            DeliveryPersonId.generate(),
            request.tenantId(),
            request.name(),
            request.phoneNumber(),
            request.vehicleType(),
            request.capacity(),
            request.initialLocation()
        );
        
        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);
        return DeliveryPersonResponse.from(saved);
    }
    
    @Override
    public DeliveryResponse assignDelivery(AssignDeliveryRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        Delivery delivery = dispatchService.assignDelivery(
            request.tenantId(),
            request.orderId(),
            request.pickupLocation(),
            request.deliveryLocation(),
            request.orderWeight(),
            request.orderVolume()
        );
        
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    public DeliveryResponse updateDeliveryStatus(UpdateDeliveryStatusRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        Delivery delivery = dispatchService.updateDeliveryStatus(
            request.deliveryId(),
            request.newStatus(),
            request.notes()
        );
        
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    public DeliveryResponse updateDeliveryLocation(UpdateLocationRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        Delivery delivery = dispatchService.updateDeliveryLocation(
            request.deliveryId(),
            request.newLocation()
        );
        
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    public DeliveryResponse cancelDelivery(DeliveryId deliveryId, String reason) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        Delivery delivery = dispatchService.cancelDelivery(deliveryId, reason);
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    public DeliveryResponse reassignDelivery(DeliveryId deliveryId, DeliveryPersonId newDeliveryPersonId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        Objects.requireNonNull(newDeliveryPersonId, "New delivery person ID cannot be null");
        
        Delivery delivery = dispatchService.reassignDelivery(deliveryId, newDeliveryPersonId);
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(DeliveryId deliveryId) {
        Objects.requireNonNull(deliveryId, "Delivery ID cannot be null");
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found: " + deliveryId));
        
        return DeliveryResponse.from(delivery);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DeliveryPersonResponse getDeliveryPerson(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + deliveryPersonId));
        
        return DeliveryPersonResponse.from(deliveryPerson);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPersonResponse> getDeliveryPersonsForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return deliveryPersonRepository.findByTenantId(tenantId)
            .stream()
            .map(DeliveryPersonResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryPersonResponse> getAvailableDeliveryPersonsForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return deliveryPersonRepository.findAvailableByTenantId(tenantId)
            .stream()
            .map(DeliveryPersonResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return deliveryRepository.findByTenantId(tenantId)
            .stream()
            .map(DeliveryResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getActiveDeliveriesForPerson(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return dispatchService.getActiveDeliveriesForPerson(deliveryPersonId)
            .stream()
            .map(DeliveryResponse::from)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getOverdueDeliveries() {
        return dispatchService.getOverdueDeliveries()
            .stream()
            .map(DeliveryResponse::from)
            .toList();
    }
    
    @Override
    public DeliveryPersonResponse updateDeliveryPersonStatus(DeliveryPersonId deliveryPersonId, 
                                                            DeliveryPersonStatus newStatus) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(newStatus, "New status cannot be null");
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + deliveryPersonId));
        
        deliveryPerson.updateStatus(newStatus);
        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);
        
        return DeliveryPersonResponse.from(saved);
    }
    
    @Override
    public DeliveryPersonResponse updateDeliveryPersonLocation(DeliveryPersonId deliveryPersonId, 
                                                              Location newLocation) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(newLocation, "New location cannot be null");
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery person not found: " + deliveryPersonId));
        
        deliveryPerson.updateLocation(newLocation);
        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);
        
        return DeliveryPersonResponse.from(saved);
    }
}